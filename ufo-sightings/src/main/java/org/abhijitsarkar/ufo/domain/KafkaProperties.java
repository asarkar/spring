package org.abhijitsarkar.ufo.domain;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Abhijit Sarkar
 */
@ConfigurationProperties("kafka")
@Component
@Setter
public class KafkaProperties {
    private String bootstrapServers;
    private String topic;

    public String getBootstrapServers() {
        return bootstrapServers == null ? "127.0.0.1:9092" : bootstrapServers;
    }

    public String getTopic() {
        return topic == null ? "ufo" : topic;
    }
}
