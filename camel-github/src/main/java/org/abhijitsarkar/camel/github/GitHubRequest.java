package org.abhijitsarkar.camel.github;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Abhijit Sarkar
 */
@Builder
@Getter
public class GitHubRequest {
    private String repo;
    private String branch;
    private String owner;
    private String username;
    private String password;
}
