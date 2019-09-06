package org.asarkar.cache

import com.hazelcast.cache.impl.HazelcastServerCachingProvider
import com.hazelcast.client.cache.impl.HazelcastClientCachingProvider
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.config.Config
import com.hazelcast.config.EvictionPolicy
import com.hazelcast.config.NearCacheConfig
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import javax.annotation.PreDestroy
import javax.cache.CacheManager
import javax.cache.configuration.FactoryBuilder
import javax.cache.configuration.MutableCacheEntryListenerConfiguration
import javax.cache.configuration.MutableConfiguration
import javax.cache.event.CacheEntryCreatedListener
import javax.cache.event.CacheEntryEvent
import javax.cache.expiry.AccessedExpiryPolicy
import javax.cache.expiry.Duration

@Configuration
@ComponentScan
class CacheConfiguration {
    @Configuration
    @ConditionalOnProperty("KUBERNETES_SERVICE_HOST")
    class ClientCacheConfiguration {
        private val logger = LoggerFactory.getLogger(ClientCacheConfiguration::class.java)

        init {
            logger.info("Hazelcast running in client mode")
        }

        @Bean
        fun hazelcastClientConfig(): ClientConfig {
            return ClientConfig().apply {
                networkConfig.kubernetesConfig
                    .setEnabled(true)
                    .setProperty("service-dns", "hazelcast.default.svc.cluster.local")
                val ncc = getNearCacheConfig(RandApp.RAND_CACHE) ?: NearCacheConfig(RandApp.RAND_CACHE)
                // This is useful when the in-memory format of the Near Cache is different from the backing data structure.
                // This setting has no meaning on Hazelcast clients, since they have no local entries.
                ncc.isCacheLocalEntries = false
                ncc.evictionConfig.evictionPolicy = EvictionPolicy.LRU
                ncc.evictionConfig.size = 10
                // When this setting is enabled, a Hazelcast instance with a Near Cache listens for cluster-wide changes
                // on the entries of the backing data structure and invalidates its corresponding Near Cache entries.
                // Changes done on the local Hazelcast instance always invalidate the Near Cache immediately.
                ncc.isInvalidateOnChange = true
                addNearCacheConfig(ncc)
                setProperty("hazelcast.logging.type", "slf4j")
            }
        }

        @Bean
        fun jCacheManager(hazelcast: HazelcastInstance): CacheManager {
            val cachingProvider = HazelcastClientCachingProvider.createCachingProvider(hazelcast)
            return cachingProvider.cacheManager.apply {
                createCache(RandApp.RAND_CACHE, cacheConfig(false))
            }
        }
    }

    @Configuration
    @ConditionalOnProperty(name = ["KUBERNETES_SERVICE_HOST"], matchIfMissing = true)
    class ServerCacheConfiguration {
        private val logger = LoggerFactory.getLogger(ServerCacheConfiguration::class.java)

        init {
            logger.info("Hazelcast running in server mode")
        }

        @PreDestroy
        fun shutdown() {
            Hazelcast.shutdownAll();
        }

        @Bean
        fun jCacheManager(hazelcast: HazelcastInstance): CacheManager {
            val cachingProvider = HazelcastServerCachingProvider.createCachingProvider(hazelcast)
            return cachingProvider.cacheManager.apply {
                createCache(RandApp.RAND_CACHE, cacheConfig(true))
            }
        }

        @Bean
        fun hazelcastConfig(): Config {
            return Config().apply {
                setProperty("hazelcast.logging.type", "slf4j")
                networkConfig.join.multicastConfig.isEnabled = true
                networkConfig.join.kubernetesConfig.isEnabled = false
            }
        }
    }

    companion object {
        fun cacheConfig(server: Boolean): MutableConfiguration<Any, Int> {
            return MutableConfiguration<Any, Int>()
                .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.ONE_MINUTE)).apply {
                    // Hazelcast Docker container doesn't have listener class on its classpath
                    if (server) {
                        addCacheEntryListenerConfiguration(
                            MutableCacheEntryListenerConfiguration(
                                FactoryBuilder.factoryOf(
                                    RandCacheEntryCreatedListener()
                                ), null, false, true
                            )
                        )
                    }
                }
        }

        class RandCacheEntryCreatedListener : CacheEntryCreatedListener<Any, Int>, java.io.Serializable {
            companion object {
                private const val serialVersionUID: Long = 123
                private val logger = LoggerFactory.getLogger(RandCacheEntryCreatedListener::class.java)
            }

            override fun onCreated(events: MutableIterable<CacheEntryEvent<out Any, out Int>>) {
                events.forEach(this::logEvent)
            }

            private fun logEvent(e: CacheEntryEvent<out Any, out Int>) {
                logger.info("Cache entry type: {}, key: {}, value: {}", e.eventType, e.key, e.value)
            }
        }
    }
}