package org.abhijitsarkar.spring.pinterest.web

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI

/**
 * @author Abhijit Sarkar
 */
@Component
class OAuthFilter(val cache: Cache) : HandlerFilterFunction<ServerResponse, ServerResponse> {
    val logger: Logger = LoggerFactory.getLogger(OAuthFilter::class.java)

    override fun filter(request: ServerRequest, next: HandlerFunction<ServerResponse>?): Mono<ServerResponse>? {
        if (shouldFilter(request)) {
            return next?.handle(request)
        } else {
            logger.info("Unauthorized access to: {}, redirecting to OAuth endpoint.", request.path())
            return ServerResponse.temporaryRedirect(URI(OAUTH)).build()
        }
    }

    private fun shouldFilter(request: ServerRequest) =
            (request.path() == PIN && cache.get(ACCESS_TOKEN_CACHE_KEY, String::class.java) != null)
}