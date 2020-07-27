package org.asarkar.spring;

import io.netty.buffer.UnpooledByteBufAllocator;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.core.io.buffer.DataBufferUtils.release;
import static org.springframework.core.io.buffer.DataBufferUtils.retain;

/**
 * @author Abhijit Sarkar
 */
public class StringDecoder extends AbstractDecoder<String> {
    private static final IntPredicate NEWLINE_DELIMITER = b -> b == '\n' || b == '\r';

    private final long maxDurationMillis;

    public StringDecoder(long maxDurationMillis) {
        this.maxDurationMillis = maxDurationMillis;
    }

    @Override
    public Flux<String> decode(Publisher<DataBuffer> publisher, ResolvableType elementType, MimeType mimeType, Map<String, Object> hints) {
        DataBuffer incomplete = new NettyDataBufferFactory(UnpooledByteBufAllocator.DEFAULT).allocateBuffer(0);

        return Flux.from(publisher)
                .scan(Tuples.of(Flux.<DataBuffer>empty(), retain(incomplete), Instant.now()),
                        (acc, buffer) -> {
                            List<DataBuffer> results = new ArrayList<>();
                            int startIdx = 0, endIdx = 0, limit = buffer.readableByteCount();
                            Instant lastEmissionTime = acc.getT3();

                            while (startIdx < limit && endIdx != -1) {
                                endIdx = buffer.indexOf(NEWLINE_DELIMITER, startIdx);
                                int length = (endIdx == -1 ? limit : endIdx) - startIdx;

                                DataBuffer slice = buffer.slice(startIdx, length);
                                // https://jira.spring.io/browse/SPR-16351
                                byte[] slice1 = new byte[length];
                                slice.read(slice1, 0, slice1.length);

                                Instant now = Instant.now();
                                boolean maxDurationElapsed = Duration.between(lastEmissionTime, now).toMillis()
                                        >= maxDurationMillis;
                                if (endIdx != -1 || maxDurationElapsed) {
                                    byte[] slice2 = new byte[incomplete.readableByteCount()];
                                    incomplete.read(slice2, 0, slice2.length);
                                    // call retain to match release during decoding to string later
                                    // http://netty.io/wiki/reference-counted-objects.html
                                    // https://logz.io/blog/netty-bytebuf-memory-leak/
                                    results.add(retain(
                                            incomplete.factory().allocateBuffer()
                                                    .write(slice2)
                                                    .write(slice1)
                                    ));
                                    lastEmissionTime = now;
                                    if (endIdx != -1) {
                                        startIdx = endIdx + 1;
                                    }
                                } else {
                                    incomplete.write(slice1);
                                }
                            }

                            return Tuples.of(Flux.fromIterable(results), incomplete, lastEmissionTime);
                        })
                .flatMap(Tuple2::getT1)
                .map(buffer -> {
                    // charset resolution should in general use supplied mimeType
                    String s = UTF_8.decode(buffer.asByteBuffer()).toString();

                    return s;
                })
                .doOnTerminate(() -> release(incomplete))
                .log();
    }
}