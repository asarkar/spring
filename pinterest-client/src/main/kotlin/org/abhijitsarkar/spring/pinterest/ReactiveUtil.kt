package org.abhijitsarkar.spring.pinterest

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author Abhijit Sarkar
 */
fun reduceStream(flux: Flux<DataBuffer>): Mono<ByteArray> {
    return flux
            .reduce { obj, buffers -> obj.write(buffers) }
            .map { dataBuffer ->
                dataBuffer.readableByteCount()
                        .let { ByteArray(it) }
                        .also { dataBuffer.read(it) }
                        .apply { DataBufferUtils.release(dataBuffer) }
            }
}