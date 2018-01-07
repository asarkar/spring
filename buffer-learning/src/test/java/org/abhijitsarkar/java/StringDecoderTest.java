package org.abhijitsarkar.java;

import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Abhijit Sarkar
 */
public class StringDecoderTest {
    DataBufferFactory dataBufferFactory = new NettyDataBufferFactory(UnpooledByteBufAllocator.DEFAULT);

    @Test
    public void testDecode() {
        StringDecoder stringDecoder = new StringDecoder(5000L);
        Flux<DataBuffer> pub = Flux.just("abc\n", "abc", "def\n", "abc", "def\nxyz\n", "abc", "def", "xyz\n")
                .map(s -> dataBufferFactory.wrap(s.getBytes(UTF_8)));

        StepVerifier.create(stringDecoder.decode(pub, null, null, null))
                .expectNext("abc", "abcdef", "abcdef", "xyz", "abcdefxyz")
                .verifyComplete();
    }

    @Test
    public void testDecodeTimeout() {
        StringDecoder stringDecoder = new StringDecoder(10L);

        Flux<DataBuffer> pub = Flux.just("abc\n", "def")
                .map(s -> dataBufferFactory.wrap(s.getBytes(UTF_8)))
                .delayElements(Duration.ofMillis(10L));

        StepVerifier.create(stringDecoder.decode(pub, null, null, null))
                .expectNext("abc", "def")
                .verifyComplete();
    }
}