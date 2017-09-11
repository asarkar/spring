package org.abhijitsarkar.spring.pinterest

import io.kotlintest.specs.ShouldSpec
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.AccessTokenRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.CreateBoardRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.CreatePinRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.DeleteBoardRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.FindBoardRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.FindUserRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.FindUserResponse
import org.abhijitsarkar.spring.pinterest.client.PinterestDefaultImpl
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.io.File
import java.time.Duration

/**
 * @author Abhijit Sarkar
 */
class PinterestDefaultImplSpec : ShouldSpec() {
    val pinterest = PinterestDefaultImpl()

    // https://developers.pinterest.com/tools/access_token/
    val accessToken = System.getenv("PINTEREST_ACCESS_TOKEN")

    init {
        should("get the access token") {
            val clientId = System.getenv("PINTEREST_CLIENT_ID")
            val clientSecret = System.getenv("PINTEREST_CLIENT_SECRET")
            val accessCode = System.getenv("PINTEREST_ACCESS_CODE")

            StepVerifier.create(pinterest.getAccessToken(AccessTokenRequest(clientId, clientSecret, accessCode)))
                    .expectNextCount(1)
                    .expectComplete()
                    .verify(Duration.ofSeconds(3))
        }.config(enabled = false)

        should("find board and delete it if exists, then create the board, then upload a pin") {
            val name = "test"
            val response = pinterest.findUser(FindUserRequest(accessToken))
                    .map(FindUserResponse::username)
                    .flatMap { username ->
                        pinterest.findBoard(FindBoardRequest(accessToken, name, username))
                                .hasElement()
                                .defaultIfEmpty(false)
                                .map { it.to(username) }
                    }
                    .flatMap { (found, username) ->
                        Mono.just(found)
                                .filter { it }
                                .flatMap { fnd ->
                                    pinterest.deleteBoard(DeleteBoardRequest(accessToken, name, username))
                                            .map { fnd.to(username) }
                                }
                                .defaultIfEmpty(found.to(username))
                    }
                    .flatMap { (_, username) ->
                        pinterest.createBoard(CreateBoardRequest(accessToken, name, username))
                                .map { it.to(username) }
                    }
                    .flatMap { (createBoardResponse, username) ->
                        pinterest.createPin(CreatePinRequest(accessToken, createBoardResponse.name,
                                File(javaClass.getResource("/pinterest-multi-client.jpg").toURI()), username = username))
                    }

            StepVerifier.create(response)
                    .expectNextCount(1)
                    .expectComplete()
                    .verify(Duration.ofSeconds(30))
        }.config(enabled = false)
    }
}