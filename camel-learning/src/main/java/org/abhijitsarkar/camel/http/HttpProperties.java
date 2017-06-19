package org.abhijitsarkar.camel.http;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static org.apache.camel.component.http4.HttpMethods.PUT;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;

/**
 * @author Abhijit Sarkar
 */
@ConfigurationProperties("outbound.http")
@Data
@Component
public class HttpProperties {
    private String acceptHeader = APPLICATION_JSON_VALUE;
    private String contentTypeHeader = APPLICATION_OCTET_STREAM_VALUE;
    private String method = PUT.name();
    private String username = "admin";
    private String password = "password";
    private String hostAndPort = "localhost:8081";
    private String path = "/artifactory/test-repo/{filename}";
    private int connectTimeoutMillis = 500;
    private int readTimeoutMillis = 5000;
}