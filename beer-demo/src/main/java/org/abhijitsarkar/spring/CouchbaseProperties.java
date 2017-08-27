package org.abhijitsarkar.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Abhijit Sarkar
 */
@ConfigurationProperties("couchbase")
@Data
public class CouchbaseProperties {
    private String adminUsername;
    private String adminPassword;
    private String bucketName;
    private String bucketPassword;
}
