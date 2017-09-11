package org.abhijitsarkar.spring.pinterest.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router

/**
 * @author Abhijit Sarkar
 */
@Configuration
class OAuthRoutes(val oAuthHandler: OAuthHandler) {
    @Bean
    fun oAuthRouter() = router {
        (accept(APPLICATION_JSON)).nest {
            GET(OAUTH, oAuthHandler::redirect)
            GET(OAUTH_TOKEN, oAuthHandler::accessToken)
        }
    }
}