package org.abhijitsarkar.spring.pinterest.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

/**
 * @author Abhijit Sarkar
 */
@Configuration
class BoardRoutes(val boardHandler: BoardHandler, val oAuthFilter: OAuthFilter) {
    @Bean
    fun boardRouter() = router {
        (accept(MediaType.APPLICATION_JSON)).nest {
            GET(BOARD, boardHandler::find)
            POST(BOARD, boardHandler::create)
        }
    }
            .filter(oAuthFilter)
}