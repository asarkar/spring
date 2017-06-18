package org.abhijitsarkar.springintegration.http;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

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
    private String baseUrl = "http://localhost:8081";
    private String path = "/artifactory/test-repo/{filename}";
    private int connectTimeout = 500;
    private int readTimeout = 1000 * 10;
}
