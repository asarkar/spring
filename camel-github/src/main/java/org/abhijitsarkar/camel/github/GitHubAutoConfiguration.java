package org.abhijitsarkar.camel.github;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Abhijit Sarkar
 */
@Configuration
@Slf4j
public class GitHubAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public GitHub gitHub() {
        return Feign.builder()
                .logger(new Slf4jLogger(log.getName()))
                .logLevel(Logger.Level.FULL)
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder())
                .target(GitHub.class, "https://api.github.com");
    }
}
