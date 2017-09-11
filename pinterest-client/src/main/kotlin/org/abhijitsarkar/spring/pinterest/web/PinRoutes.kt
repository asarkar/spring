package org.abhijitsarkar.spring.pinterest.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

/**
 * @author Abhijit Sarkar
 */
@Configuration
class PinRoutes(val pinHandler: PinHandler, val oAuthFilter: OAuthFilter) {
    @Bean
    fun pinRouter() = router {
        (accept(MediaType.MULTIPART_FORM_DATA)).nest {
            POST(PIN, pinHandler::create)
            GET(PIN, pinHandler::indexPage)
        }
    }
            .filter(oAuthFilter)
}