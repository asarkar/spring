package org.abhijitsarkar.camel;

import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

/**
 * @author Abhijit Sarkar
 */
@Configuration
public class TestConfiguration {
    @Bean
    Supplier<OutputStream> streamConsumer() {
        return new ByteArrayOutputStreamConsumer();
    }

    @Getter
    public static class ByteArrayOutputStreamConsumer implements Supplier<OutputStream> {
        private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

        public void reset() {
            bos.reset();
        }

        @Override
        public OutputStream get() {
            return bos;
        }
    }
}
