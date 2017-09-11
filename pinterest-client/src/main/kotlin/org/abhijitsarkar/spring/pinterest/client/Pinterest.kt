package org.abhijitsarkar.spring.pinterest.client

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.HttpUrl
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.AccessTokenRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.AccessTokenResponse
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.CreateBoardRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.CreateBoardResponse
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.CreatePinRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.CreatePinResponse
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.DeleteBoardRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.DeleteBoardResponse
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.FindBoardRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.FindBoardResponse
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.FindUserRequest
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.FindUserResponse
import org.abhijitsarkar.spring.pinterest.client.Pinterest.PinterestJsonFormat.ResponseWrapper
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.io.File
import java.nio.charset.StandardCharsets


/**
 * @author Abhijit Sarkar
 */

interface Pinterest {
    fun getAccessToken(request: AccessTokenRequest): Mono<AccessTokenResponse>
    fun createBoard(request: CreateBoardRequest): Mono<CreateBoardResponse>
    fun deleteBoard(request: DeleteBoardRequest): Mono<DeleteBoardResponse>
    fun findBoard(request: FindBoardRequest): Mono<FindBoardResponse>
    fun createPin(request: CreatePinRequest): Mono<CreatePinResponse>
    fun findUser(request: FindUserRequest): Mono<FindUserResponse>

    companion object PinterestJsonFormat {
        data class AccessTokenRequest(
                val clientId: String,
                val clientSecret: String,
                val accessCode: String
        )

        data class CreateBoardRequest(val accessToken: String, val name: String, val description: String = "New Board")

        data class CreatePinRequest(val accessToken: String, val board: String, val file: File,
                                    val description: String = "New Pin", val username: String)

        data class FindBoardRequest(val accessToken: String, val name: String, val username: String)

        data class DeleteBoardRequest(val accessToken: String, val name: String, val username: String)

        data class FindUserRequest(val accessToken: String)

        data class ResponseWrapper<out T>(val data: T)

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class AccessTokenResponse(@JsonProperty("access_token") val accessToken: String)

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class CreateBoardResponse(val id: String, val url: String, val name: String)

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class DeleteBoardResponse(val name: String)

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class FindBoardResponse(val id: String, val name: String)

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class CreatePinResponse(val id: String, val url: String)

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class FindUserResponse(val url: String) {
            @JsonIgnore
            val username = if (url.endsWith("/")) {
                url.dropLast(1)
            } else {
                url
            }.takeLastWhile { it != '/' }
        }
    }
}

@Component
class PinterestDefaultImpl() : Pinterest {
    val webClient: WebClient = WebClient.builder()
            .baseUrl("https://api.pinterest.com/v1/")
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()
    val mapper: ObjectMapper = ObjectMapper()
            .registerKotlinModule()

    fun HttpStatus.isNotFound(): Boolean = this.value() == 404

    private inline fun <reified T> ClientResponse.toMono(): Mono<T> {
        return when {
            this.statusCode().isNotFound() -> return Mono.empty()
            this.statusCode().isError -> this.body(BodyExtractors.toDataBuffers())
                    .reduce { obj, buffers -> obj.write(buffers) }
                    .map { dataBuffer ->
                        dataBuffer.readableByteCount()
                                .let { ByteArray(it) }
                                .also { dataBuffer.read(it) }
                                .apply { DataBufferUtils.release(dataBuffer) }
                    }
                    .map { bodyBytes ->
                        val msg = "${this.statusCode().value()} ${this.statusCode().reasonPhrase}"
                        val charset = this.headers().contentType()
                                .map { it.charset }
                                .orElse(StandardCharsets.UTF_8)
                        WebClientResponseException(
                                msg,
                                this.statusCode().value(),
                                this.statusCode().reasonPhrase,
                                this.headers().asHttpHeaders(),
                                bodyBytes,
                                charset
                        )
                    }
                    .flatMap { Mono.error<T>(it) }
            else -> this.bodyToMono(T::class.java)
        }
    }

    override fun getAccessToken(request: AccessTokenRequest): Mono<AccessTokenResponse> {
        return webClient.post()
                .uri { builder ->
                    builder
                            .path("/oauth/token")
                            .build()
                }
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(
                        LinkedMultiValueMap<String, String>()
                                .apply {
                                    add("grant_type", "authorization_code")
                                    add("client_id", request.clientId)
                                    add("client_secret", request.clientSecret)
                                    add("code", request.accessCode)
                                }
                ))
                .exchange()
                .flatMap { it.toMono<String>() }
                .map { mapper.readValue<AccessTokenResponse>(it) }
    }

    override fun findBoard(request: FindBoardRequest): Mono<FindBoardResponse> {
        return webClient.get()
                .uri { builder ->
                    builder
                            .path("/boards/${request.username}/${request.name}/")
                            .queryParam("access_token", request.accessToken)
                            .build()
                }
                .exchange()
                .flatMap { it.toMono<String>() }
                .map { mapper.readValue<ResponseWrapper<FindBoardResponse>>(it) }
                .map { it.data }
    }

    override fun createBoard(request: CreateBoardRequest): Mono<CreateBoardResponse> {
        return webClient.post()
                .uri { builder ->
                    builder
                            .path("/boards/")
                            .queryParam("access_token", request.accessToken)
                            .build()
                }
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(
                        LinkedMultiValueMap<String, String>()
                                .apply {
                                    add("name", request.name)
                                    add("description", request.description)
                                }
                ))
                .exchange()
                .flatMap { it.toMono<String>() }
                .map { mapper.readValue<ResponseWrapper<CreateBoardResponse>>(it) }
                .map { it.data }
    }

    override fun deleteBoard(request: DeleteBoardRequest): Mono<DeleteBoardResponse> {
        return webClient.delete()
                .uri { builder ->
                    builder
                            .path("/boards/${request.username}/${request.name}/")
                            .queryParam("access_token", request.accessToken)
                            .build()
                }
                // https://jira.spring.io/browse/SPR-15947
                .header(HttpHeaders.TRANSFER_ENCODING, null)
                .exchange()
                .flatMap { it.toMono<String>() }
                .map { DeleteBoardResponse(request.name) }
    }

    override fun createPin(request: CreatePinRequest): Mono<CreatePinResponse> {
        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("board", "${request.username}/${request.board}")
                .addFormDataPart("note", request.description)
                .addFormDataPart("image", request.file.name,
                        RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_OCTET_STREAM_VALUE),
                                request.file))
                .build()

        val url = HttpUrl.Builder()
                .scheme("https")
                .host("api.pinterest.com")
                .addPathSegments("v1/pins/")
                .addQueryParameter("access_token", request.accessToken)
                .build()

        val req = Request.Builder()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .url(url)
                .post(requestBody)
                .build()

        return Mono.create<CreatePinResponse> { consumer ->
            try {
                val response = OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build()
                        .newCall(req)
                        .execute()

                response.use {
                    if (it.isSuccessful) {
                        consumer.success(it.body()!!.string().let {
                            mapper.readValue<ResponseWrapper<CreatePinResponse>>(it)
                        }
                                .data)
                    } else {
                        consumer.error(RuntimeException("${it.code()} ${it.message()}"))
                    }
                }
            } catch (e: Exception) {
                consumer.error(e)
            }
        }
        // https://jira.spring.io/browse/SPR-15948

//        return webClient.post()
//                .uri { builder ->
//                    builder
//                            .path("/pins/")
//                            .queryParam("access_token", request.accessToken)
//                            .queryParam("board", "${request.username}/${request.board}")
//                            .queryParam("note", request.description)
//                            .build()
//                }
//                .body(BodyInserters.fromFormData(
//                        LinkedMultiValueMap<String, String>()
//                                .apply {
//                                    add("board", "${request.username}/${request.board}")
//                                    add("note", request.description)
//                                    add("image_url", "file:/Users/asarkar/Workspace/spring/pinterest-client/src/test/resources/test.png")
//                                }
//                ))
//                .body(BodyInserters.fromMultipartData(
//                        LinkedMultiValueMap<String, Any>()
//                                .apply {
//                                    add("image", request.entity)
//                                }
//                ))
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
//                .header(HttpHeaders.TRANSFER_ENCODING, null)
//                .header(HttpHeaders.ACCEPT_ENCODING, null)
//                .exchange()
//                .flatMap { it.toMono<String>() }
//                .map { mapper.readValue<ResponseWrapper<CreatePinResponse>>(it) }
//                .map { it.data }
    }

    override fun findUser(request: FindUserRequest): Mono<FindUserResponse> {
        return webClient.get()
                .uri { builder ->
                    builder
                            .path("/me/")
                            .queryParam("access_token", request.accessToken)
                            .build()
                }
                .exchange()
                .flatMap { it.toMono<String>() }
                .map { mapper.readValue<ResponseWrapper<FindUserResponse>>(it) }
                .map { it.data }
    }
}