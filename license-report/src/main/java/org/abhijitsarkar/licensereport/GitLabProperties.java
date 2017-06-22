package org.abhijitsarkar.licensereport;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abhijit Sarkar
 */
@ConfigurationProperties("gitlab")
@Data
public class GitLabProperties {
    private String baseUrl;
    private List<GroupProperties> groups = new ArrayList<>();
    private ConnectionProperties connection = new ConnectionProperties();

    @Data
    public static class GroupProperties {
        private String name;
        private String privateToken;
        private List<String> excludesProjects = new ArrayList<>();
        private List<String> includesProjects = new ArrayList<>();
    }

    @Data
    public static class ConnectionProperties {
        private int connectTimeoutMillis = 500;
        private int readTimeoutMillis = 5000;
    }
}
