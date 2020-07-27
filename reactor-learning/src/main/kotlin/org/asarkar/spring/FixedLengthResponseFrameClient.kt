package org.asarkar.spring

import reactor.netty.http.client.HttpClient

object FixedLengthResponseFrameClient {
    fun get(url: String, maxChunkSize: Int): List<ByteArray> {
        return HttpClient.create()
            .httpResponseDecoder { it.maxChunkSize(maxChunkSize) }
            .get()
            .uri(url)
            .responseContent()
            .asByteArray()
            .collectList()
            .block()!!
    }
}