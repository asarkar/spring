package org.abhijitsarkar;

import lombok.Data;
import org.springframework.boot.autoconfigure.social.SocialProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Abhijit Sarkar
 */
@Component
@ConfigurationProperties(prefix = "spring.social.facebook")
@Data
public class MoreFacebookProperties extends SocialProperties {
    private String apiVersion;
    private String appNamespace;
}
