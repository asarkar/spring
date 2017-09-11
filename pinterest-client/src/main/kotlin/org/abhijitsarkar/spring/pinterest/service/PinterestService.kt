package org.abhijitsarkar.spring.pinterest.service

import org.abhijitsarkar.spring.pinterest.client.Pinterest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.AccessTokenRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.CreateBoardRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.CreatePinRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.FindUserRequest
import org.abhijitsarkar.spring.pinterest.web.PinHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * @author Abhijit Sarkar
 */
@Service
class PinterestService(val pinterest: Pinterest) {
    val logger: Logger = LoggerFactory.getLogger(PinHandler::class.java)

    fun findOrCreateBoard(boardName: String, accessToken: String): Mono<Pair<Boolean, String>> {
        return pinterest.findUser(FindUserRequest(accessToken))
                .map(Pinterest.PinterestJsonFormat.FindUserResponse::username)
                .doOnNext { logger.info("Found user: {}.", it) }
                .flatMap { username ->
                    pinterest.findBoard(Pinterest.PinterestJsonFormat.FindBoardRequest(accessToken, boardName, username))
                            .doOnNext { logger.info("Found board: {}.", it.name) }
                            .hasElement()
                            .switchIfEmpty(Mono.just(false)
                                    .doOnNext { logger.info("Board not found: {}.", boardName) })
                            .map { it.to(username) }
                }
                .flatMap { (found, username) ->
                    Mono.just(found)
                            .filter { !it }
                            .flatMap { fnd ->
                                pinterest.createBoard(CreateBoardRequest(accessToken, boardName, username))
                                        .map { fnd.to(username) }
                                        .doOnNext { logger.info("Created board: {}.", boardName) }
                            }
                            .defaultIfEmpty(!found to username)
                }
                .doOnError { logger.error("Something went wrong with board operations!", it) }
    }

    fun createPin(request: CreatePinRequest) = pinterest.createPin(request)

    fun getAccessToken(request: AccessTokenRequest) = pinterest.getAccessToken(request)
}