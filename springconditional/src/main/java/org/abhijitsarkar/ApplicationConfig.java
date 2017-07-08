package org.abhijitsarkar;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Abhijit Sarkar
 */
@Configuration
@PropertySource("classpath:/application.properties")
@ComponentScan
public class ApplicationConfig {

    @FunctionalInterface
    public static interface GreeterService {
        String greeting();
    }

    @Bean
    @ConditionalOnProperty("hello")
    public GreeterService helloService() {
        return () -> "hello";
    }

    @Bean
    @ConditionalOnProperty("hi")
    public GreeterService hiService() {
        return () -> "hi";
    }
}
