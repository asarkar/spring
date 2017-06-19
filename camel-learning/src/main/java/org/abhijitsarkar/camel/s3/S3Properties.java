package org.abhijitsarkar.camel.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Abhijit Sarkar
 */
@ConfigurationProperties("inbound.s3")
@Data
@Component
public class S3Properties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;
    private String prefix;
    private long lastModifiedWithinSeconds = 60 * 60 * 48; // 2 days
    private int maxFetchSize = 10;
    private int connectTimeoutMillis = 500;
    private int readTimeoutMillis = 1000 * 60 * 30;
    private int maxIdleMillis = 1000 * 60 * 10;
    private int maxConnections = 50;
}