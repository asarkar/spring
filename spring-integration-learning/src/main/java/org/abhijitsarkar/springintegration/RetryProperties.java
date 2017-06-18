package org.abhijitsarkar.springintegration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Abhijit Sarkar
 */
@Data
@ConfigurationProperties("retry")
public class RetryProperties {
    private ExponentialBackOff exponentialBackOff = new ExponentialBackOff();

    @Data
    public static class ExponentialBackOff {
        private long initialIntervalMillis = 1000;
        private long maxIntervalMillis = 1000 * 60;
        private double multiplier = 5;
    }
}
