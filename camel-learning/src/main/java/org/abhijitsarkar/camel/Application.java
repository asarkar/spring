package org.abhijitsarkar.camel;

import lombok.Getter;
import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

/**
 * @author Abhijit Sarkar
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new SpringApplicationBuilder(Application.class)
                .web(false)
                .run(args);

        CamelSpringBootApplicationController applicationController =
                applicationContext.getBean(CamelSpringBootApplicationController.class);
        applicationController.run();
    }

    @Bean
    @Profile("default")
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
