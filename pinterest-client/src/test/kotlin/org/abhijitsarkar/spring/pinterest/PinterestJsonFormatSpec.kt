package org.abhijitsarkar.spring.pinterest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.CreateBoardResponse
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.CreatePinResponse
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.FindBoardResponse
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.FindUserResponse
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.ResponseWrapper

/**
 * @author Abhijit Sarkar
 */
class PinterestJsonFormatSpec : ShouldSpec() {
    val mapper: ObjectMapper = ObjectMapper()
            .registerKotlinModule()

    init {
        should("deserialize find board response") {
            val json = javaClass.getResourceAsStream("/find-board.json")

            val board = mapper.readValue<ResponseWrapper<FindBoardResponse>>(json)

            board.data.name shouldBe "test"
        }

        should("deserialize find user response") {
            val json = javaClass.getResourceAsStream("/find-user.json")
            val user = mapper.readValue<ResponseWrapper<FindUserResponse>>(json)

            user.data.url shouldBe "https://www.pinterest.com/test/"
        }


        should("deserialize create board response") {
            val json = javaClass.getResourceAsStream("/create-board.json")
            val user = mapper.readValue<ResponseWrapper<CreateBoardResponse>>(json)

            user.data.url shouldBe "https://www.pinterest.com/test/test/"
        }

        should("deserialize create pin response") {
            val json = javaClass.getResourceAsStream("/create-pin.json")
            val user = mapper.readValue<ResponseWrapper<CreatePinResponse>>(json)

            user.data.url shouldBe "https://www.pinterest.com/pin/123/"
        }
    }
}