package org.abhijitsarkar.camel.github;

import java.util.List;

/**
 * @author Abhijit Sarkar
 */
public interface GitHubClient {
    List<String> getCommitsForARepo(GitHubRequest request, int limit);

    List<String> getAllFilesForACommit(GitHubRequest request, String sha);
}
