package org.abhijitsarkar.coolhttpclient;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Abhijit Sarkar
 */
@Component
@ConfigurationProperties("coolHttp")
@Data
public class CoolHttpClientProperties {
    private int maxIdleConnections = 5;
    private long keepAliveMillis = 1000 * 5 * 60;
    private long connectTimeoutMillis = 500;
    private long readTimeoutMillis = 2000;
    private boolean followRedirects;
}
