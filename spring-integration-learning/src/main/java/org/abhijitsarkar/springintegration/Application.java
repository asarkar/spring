package org.abhijitsarkar.springintegration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;

@SpringBootApplication
@EnableIntegration
@Slf4j
public class Application {
    public static final String INPUT_CHANNEL = "inputChannel";
    public static final String OUTPUT_GATEWAY = "outputGateway";

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    public IntegrationFlow flow() {
        return IntegrationFlows.from(INPUT_CHANNEL)
                .transform(Transformers.fromStream())
                .gateway(OUTPUT_GATEWAY)
                .get();
    }
}
