package org.abhijitsarkar.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.UnknownHostException

/**
 * @author Abhijit Sarkar
 */

interface LinkVerifier {
    fun isValid(link: String): Mono<Boolean>
}

internal class LinkVerifierImpl private constructor(val webClient: WebClient, val cache: Cache<String, Boolean>) : LinkVerifier {
    companion object {
        private fun cache() = Caffeine.newBuilder()
                .maximumSize(10000)
                .build<String, Boolean>()

        fun newInstance(webClient: WebClient = org.abhijitsarkar.webClient(), cache: Cache<String, Boolean> = cache()): LinkVerifier =
                LinkVerifierImpl(webClient, cache)
    }

    private val log = LoggerFactory.getLogger(LinkVerifier::class.java)

    override fun isValid(link: String): Mono<Boolean> {
        val split = link.split(":".toRegex(), 2)

        if (split.size < 2) return Mono.just(false)

        val path = split[1].drop(2)

        return Mono.justOrEmpty(cache.getIfPresent(path))
                .map { it as Boolean } // Tell overzealous compiler that it can't be null
                .switchIfEmpty(webClient
                        .head()
                        .uri(link)
                        .exchange()
                        .retry(2L, {
                            when (it) {
                                is UnknownHostException -> false
                                else -> true
                            }
                        })
                        .map(ClientResponse::statusCode)
                        .map { it.value() < 400 }
                        .doOnSuccess { cache.put(path, it) }
                        .onErrorResume { t -> log.error("Failed to verify link: {}.", link, t); Mono.just(false) }
                )
    }
}

//    private fun sslEngine(): SSLEngine? {
//        val tms = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
//                .apply { init(null as KeyStore?) }
//                .run { trustManagers }
//                .let {
//                    it?.forEachIndexed { index, trustManager ->
//                        if (trustManager is X509TrustManager) {
//                            it[index] = TrustManagerDelegate(trustManager)
//                        }
//                    }
//                    it
//                }
//
//        return SSLContext.getInstance("TLS")
//                .apply { init(null, tms, null) }
//                .run { createSSLEngine() }
//                .apply { useClientMode = true }
//    }
//
//    private class TrustManagerDelegate(val trustManager: X509TrustManager) : X509TrustManager by trustManager {
//        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String?) {
//            try {
//                trustManager.checkServerTrusted(chain, authType)
//            } catch (ex: CertificateException) {
//                val cn = chain
//                        .map { it.issuerX500Principal.name }
//                        .flatMap { name ->
//                            try {
//                                LdapName(name)
//                                        .rdns
//                                        .filter { it.type.equals("cn", true) }
//                                        .map { it.value.toString() }
//                            } catch (ex: InvalidNameException) {
//                                emptyList<String>()
//                            }
//                        }
//                        .joinToString(", ")
//
//                log.warn("CertificateException: CN[$cn].", ex)
//            }
//        }
//    }
