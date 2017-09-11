package org.abhijitsarkar.spring.pinterest.web

import org.abhijitsarkar.spring.pinterest.client.Pinterest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.CreateBoardRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.FindBoardRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.FindUserRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.FindUserResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI

/**
 * @author Abhijit Sarkar
 */
@Component
class BoardHandler(val pinterest: Pinterest, val cache: Cache) {
    val logger: Logger = LoggerFactory.getLogger(BoardHandler::class.java)
    val accessToken: String get() = cache.get(ACCESS_TOKEN_CACHE_KEY, String::class.java)

    fun create(request: ServerRequest): Mono<ServerResponse> {
        val name = request.pathVariable("name")

        return pinterest.createBoard(CreateBoardRequest(accessToken, name = name))
                .doOnNext { logger.info("Created board: {}.", it.url) }
                .flatMap {
                    ServerResponse.created(URI.create(it.url))
                            .build()
                }
                .onErrorResume {
                    logger.error("Failed to create board: {}.", name, it)
                    ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(BodyInserters.fromObject(it.message))
                }
    }

    fun find(request: ServerRequest): Mono<ServerResponse> {
        val name = request.pathVariable("name")

        return pinterest.findUser(FindUserRequest(accessToken))
                .map(FindUserResponse::username)
                .doOnNext { logger.info("Found user: {}.", it) }
                .flatMap { username ->
                    pinterest.findBoard(FindBoardRequest(accessToken, name, username))
                            .doOnNext { logger.info("Found board: {}.", it.name) }
                            .flatMap { ServerResponse.ok().build() }
                            .switchIfEmpty(ServerResponse.notFound().build()
                                    .doOnNext { logger.info("Board not found: {}.", name) })
                            .onErrorResume {
                                logger.error("Failed to find board: {}.", name, it)
                                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(BodyInserters.fromObject(it.message))
                            }
                }
    }
}
