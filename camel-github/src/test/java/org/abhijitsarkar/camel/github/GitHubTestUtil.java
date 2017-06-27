package org.abhijitsarkar.camel.github;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Abhijit Sarkar
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GitHubTestUtil {
    public static void configureMock(GitHub gitHubClient) {
        GitHub.Repository repo1 = new GitHub.Repository();
        repo1.setName("mockRepo1");
        GitHub.Repository repo2 = new GitHub.Repository();
        repo2.setName("mockRepo2");

        when(gitHubClient.repos("username"))
                .thenReturn(asList(repo1, repo2));

        GitHub.Commit commit1 = newCommit("mockSha1");
        GitHub.Commit commit2 = newCommit("mockSha2");

        when(gitHubClient.commits("username", "mockRepo1"))
                .thenReturn(asList(commit1, commit2));

        GitHub.Commit commit3 = newCommit("mockSha3");
        GitHub.Commit commit4 = newCommit("mockSha4");

        when(gitHubClient.commits("username", "mockRepo2"))
                .thenReturn(asList(commit3, commit4));

        when(gitHubClient.commit(anyString(), anyString(), anyString()))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    String username = args[0].toString();
                    String repo = args[1].toString();
                    String sha = args[2].toString();

                    if (!username.equals("username")) {
                        return null;
                    }

                    if (!repo.equals("mockRepo1") && !repo.equals("mockRepo2")) {
                        return null;
                    }

                    if (sha.equals("mockSha1")) {
                        return commit1;
                    }
                    if (sha.equals("mockSha2")) {
                        return commit2;
                    }
                    if (sha.equals("mockSha3")) {
                        return commit3;
                    }
                    if (sha.equals("mockSha4")) {
                        return commit4;
                    }

                    return null;
                });
    }

    static GitHub.Commit newCommit(String sha) {
        GitHub.Commit commit = new GitHub.Commit();
        commit.setSha(sha);

        GitHub.Commit.Committer committer = new GitHub.Commit.Committer();
        committer.setName("John Doe");
        committer.setDateTime(ISO_INSTANT.format(Instant.now()));

        GitHub.Commit innerCommit = new GitHub.Commit();
        innerCommit.setMessage("Test");
        innerCommit.setCommitter(committer);
        commit.setCommit(innerCommit);

        GitHub.Commit.File file1 = new GitHub.Commit.File();
        file1.setFilename("file1");
        GitHub.Commit.File file2 = new GitHub.Commit.File();
        file2.setFilename("file2");
        commit.setFiles(asList(file1, file2));

        return commit;
    }
}
