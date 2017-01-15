package org.abhijitsarkar.ig.service;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Abhijit Sarkar
 */
@Component
@ConfigurationProperties("instagram")
@Data
public class IgProperties {
    private String authorizeUrl;
    private String accessTokenUrl;
    private String recentPostsUrl;
}
