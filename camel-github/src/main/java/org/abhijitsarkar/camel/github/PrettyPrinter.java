package org.abhijitsarkar.camel.github;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.abhijitsarkar.camel.github.Application.REPO;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrettyPrinter {
    public static void print(Exchange e) {
        Message in = e.getIn();

        String repo = in.getHeader(REPO, String.class);
        List<GitHub.Commit> commits = e.getIn().getBody(List.class);

        System.out.format("===\n");
        System.out.format("%s\n", repo);

        commits.forEach(commit -> {
            System.out.format("|---SHA: %s\n", commit.getSha());
            GitHub.Commit.Committer committer = commit.getCommitter();
            System.out.format("|---COMMITTER: %s\n", committer.getName());
            System.out.format("|---DATETIME: %s\n", committer.getDateTime());
            System.out.format("|---MESSAGE: %s\n", commit.getMessage().replaceAll("\\s+", " "));
            System.out.format("|---FILES: %s\n\n", commit.getFiles().stream()
                    .map(GitHub.Commit.File::getFilename)
                    .collect(joining(", "))
            );
        });
        System.out.format("===\n");
    }
}
