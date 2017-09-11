package org.abhijitsarkar.spring.pinterest.web

import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.CreatePinRequest
import org.abhijitsarkar.spring.pinterest.reduceStream
import org.abhijitsarkar.spring.pinterest.service.PinterestService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.Cache
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.TEXT_HTML
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.Part
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI
import java.nio.file.Files

/**
 * @author Abhijit Sarkar
 */
@Component
class PinHandler(val pinterestService: PinterestService, val cache: Cache) {
    val logger: Logger = LoggerFactory.getLogger(PinHandler::class.java)
    val accessToken: String get() = cache.get(ACCESS_TOKEN_CACHE_KEY, String::class.java)
    val defaultBoardName = "Whatever"

    @Value("classpath:/static$PINTEREST/index.html")
    private lateinit var indexHtml: Resource

    fun indexPage(request: ServerRequest): Mono<ServerResponse> =
            ServerResponse.ok().contentType(TEXT_HTML).syncBody(indexHtml)

    fun create(request: ServerRequest): Mono<ServerResponse> {
        val boardName = request.queryParam("boardName").orElseGet {
            defaultBoardName.also {
                logger.warn("'boardName' query parameter not found, defaulting to: {}.", defaultBoardName)
            }
        }

        return pinterestService.findOrCreateBoard(boardName, accessToken)
                .doOnNext { logger.debug("Processing multipart request.") }
                .flatMap { (_, username) ->
                    request.body(BodyExtractors.toMultipartData())
                            .flatMap {
                                val filePart: Part? = it.toSingleValueMap()["files"]
                                filePart!!.content()
                                        .let { reduceStream(it) }
                                        .flatMap { bodyBytes ->
                                            val filename = (filePart as? FilePart)?.filename()
                                            val extension = filename?.takeLastWhile { it != '.' }?.let { ".$it" }

                                            logger.debug("Found filename: {}, deduced extension: {}.", filename, extension)

                                            val tmpFile = Files.createTempFile(null, extension)
                                                    .also { Files.newOutputStream(it).use { it.write(bodyBytes) } }

                                            val req = CreatePinRequest(accessToken, boardName, tmpFile.toFile(),
                                                    username = username)

                                            pinterestService.createPin(req)
                                                    .doOnNext { logger.info("Created pin: {}.", it.url) }
                                                    .flatMap {
                                                        ServerResponse.created(URI.create(it.url))
                                                                .build()
                                                    }
                                                    .onErrorResume {
                                                        logger.error("Failed to create pin: {}.", it)
                                                        ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                                .body(BodyInserters.fromObject(it.message))
                                                    }
                                        }
                            }
                }
                .switchIfEmpty(ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(BodyInserters.fromObject("Not supposed to be empty.")))
    }
}