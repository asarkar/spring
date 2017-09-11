package org.abhijitsarkar.spring.pinterest.web

import org.abhijitsarkar.spring.pinterest.client.Pinterest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.AccessTokenRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.AccessTokenResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.Cache
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.net.URI

/**
 * @author Abhijit Sarkar
 */
@Component
class OAuthHandler(val pinterest: Pinterest, val cache: Cache) {
    val logger: Logger = LoggerFactory.getLogger(OAuthHandler::class.java)
    val state = "state"

    @Value("\${pinterest.clientId}")
    lateinit var clientId: String

    @Value("\${pinterest.clientSecret}")
    lateinit var clientSecret: String

    fun redirect(request: ServerRequest): Mono<ServerResponse> =
            UriComponentsBuilder.fromUriString("https://api.pinterest.com/oauth/")
                    .queryParam("response_type", "code")
                    .queryParam("scope", "read_public,write_public")
                    .queryParam("state", state)
                    .queryParam("client_id", clientId)
                    .queryParam("redirect_uri", request.uri().resolve(URI(OAUTH_TOKEN))
                            // https://jira.spring.io/browse/SPR-15931
                            .toString().replace("http", "https"))
                    .build()
                    .toUri()
                    .also { logger.debug("Redirecting to: $it.") }
                    .let { ServerResponse.permanentRedirect(it).build() }

    fun accessToken(request: ServerRequest): Mono<ServerResponse> =
            request.queryParam("state")
                    .filter { it == state }
                    .flatMap { request.queryParam("code") }
                    .toMono()
                    .flatMap { if (it.isPresent) Mono.just(it.get()) else Mono.empty() }
                    .doOnNext { logger.debug("Received access code: $it.") }
                    .flatMap { pinterest.getAccessToken(AccessTokenRequest(clientId, clientSecret, it)) }
                    .map(AccessTokenResponse::accessToken)
                    .doOnNext {
                        logger.debug("Received access token: {}.", it)
                        cache.put(ACCESS_TOKEN_CACHE_KEY, it)
                    }
                    .doOnError { logger.error("Failed to get access token.", it) }
                    .flatMap { ServerResponse.seeOther(URI(PIN)).build() }
}