package com.asarkar.spring.redis

import io.lettuce.core.ReadFrom
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.data.redis.connection.RedisClusterConfiguration
import org.springframework.data.redis.connection.RedisConfiguration
import org.springframework.data.redis.connection.RedisNode
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.util.CollectionUtils
import java.io.IOException
import java.net.InetAddress

@SpringBootApplication
@EnableAutoConfiguration(exclude = [RedisRepositoriesAutoConfiguration::class])
class App {
    private val logger = LoggerFactory.getLogger(App::class.java)

    // https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#redis:connectors
    // See Write to Master, Read from Replica
    @Bean
    fun lettuceClientConfigurationBuilderCustomizer(): LettuceClientConfigurationBuilderCustomizer {
        return LettuceClientConfigurationBuilderCustomizer {
            logger.info("Setting ReadFrom to: UPSTREAM_PREFERRED")
            it.readFrom(ReadFrom.UPSTREAM_PREFERRED)
        }
    }

    @Bean
    @ConditionalOnMissingBean(RedisConfiguration::class)
    @ConditionalOnProperty("spring.redis.sentinel.master")
    fun redisSentinelConfiguration(
        redisProperties: RedisProperties,
        env: ConfigurableEnvironment
    ): RedisSentinelConfiguration {
        val sentinelProperties = redisProperties.sentinel
        val sentinels = if (CollectionUtils.isEmpty(sentinelProperties.nodes)) {
            // Auto-detect sentinels based on Docker Compose container names
            val appName = env.getRequiredProperty("spring.application.name")
            val sentinelPrefix = env.getProperty("redis.sentinel.prefix", "${appName}_redis-sentinel")
            generateSequence(1) { it + 1 }
                .map { "${sentinelPrefix}_$it" }
                .takeWhile(this::isReachable)
                .toList()
                .also { logger.info("Detected sentinels: {}", it) }
        } else sentinelProperties.nodes

        return RedisSentinelConfiguration().apply {
            master(sentinelProperties.master)
            setSentinels(sentinels.map { parseRedisNode(it, 26379) })
            username = redisProperties.username
            redisProperties.password?.also { password = RedisPassword.of(it) }
            sentinelProperties.password?.also { sentinelPassword = RedisPassword.of(it) }
            database = redisProperties.database
        }
    }

    @Bean
    @ConditionalOnMissingBean(RedisConfiguration::class)
    @ConditionalOnProperty("redis.cluster.enabled", havingValue = "true")
    fun redisClusterConfiguration(
        redisProperties: RedisProperties,
        env: ConfigurableEnvironment
    ): RedisClusterConfiguration {
        val clusterProperties: RedisProperties.Cluster? = redisProperties.cluster
        val nodes = if (clusterProperties == null || CollectionUtils.isEmpty(clusterProperties.nodes)) {
            // Auto-detect cluster nodes based on Docker Compose container names
            val appName = env.getRequiredProperty("spring.application.name")
            val clusterPrefix = env.getProperty("redis.cluster.prefix", "${appName}_redis-cluster")
            generateSequence(1) { it + 1 }
                .map { "${clusterPrefix}_${it}_1" }
                .takeWhile(this::isReachable)
                .toList()
                .also { logger.info("Detected cluster nodes: {}", it) }
        } else clusterProperties.nodes

        return RedisClusterConfiguration().apply {
            setClusterNodes(nodes.map { parseRedisNode(it, 6379) })
            redisProperties.password?.also { password = RedisPassword.of(it) }
        }
    }

    private fun isReachable(host: String): Boolean {
        return try {
            InetAddress.getByName(host).isReachable(500)
        } catch (ex: IOException) {
            false
        }
    }

    private fun parseRedisNode(str: String, defaultPort: Int): RedisNode {
        val parts = str.split(':')
        val port = if (parts.size == 1) defaultPort
        else parts.last().toInt()

        return RedisNode(parts.first(), port)
    }
}

fun main(args: Array<String>) {
    runApplication<App>(*args)
}
