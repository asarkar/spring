package org.abhijitsarkar.springintegration.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Abhijit Sarkar
 */
@ConfigurationProperties("inbound.s3")
@Data
public class S3Properties {
    private String accessKey;
    private String secretKey;
    private String region;
    private long lastModifiedWithinSeconds = 60 * 60 * 48; // 2 days
    private String bucket;
    private int maxFetchSize = 10;
}
