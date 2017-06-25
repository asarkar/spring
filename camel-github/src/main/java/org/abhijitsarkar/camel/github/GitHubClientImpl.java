package org.abhijitsarkar.camel.github;

import java.util.Collections;
import java.util.List;

/**
 * @author Abhijit Sarkar
 */
public class GitHubClientImpl implements GitHubClient {
    @Override
    public List<String> getCommitsForARepo(GitHubRequest request, int limit) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getAllFilesForACommit(GitHubRequest request, String sha) {
        return Collections.emptyList();
    }
}
