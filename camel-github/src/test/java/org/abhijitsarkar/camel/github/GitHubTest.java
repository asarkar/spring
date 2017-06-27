package org.abhijitsarkar.camel.github;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
public class GitHubTest {
    @Test
    public void testGetCommits() {
        GitHub gitHub = Feign.builder()
                .logger(new Slf4jLogger(log.getName()))
                .logLevel(Logger.Level.FULL)
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder())
                .target(GitHub.class, "https://api.github.com");

        final String username = "asarkar";
        final int limit = 5;
        List<GitHub.Commit> commits = gitHub.repos(username).stream()
                .limit(limit)
                .map(repo -> {
                    log.info("Repo: {}", repo);
                    return new Tuple2<String, GitHub.Repository>(username, repo);
                })
                .flatMap(tuple -> {
                    List<GitHub.Commit> c = gitHub.commits(tuple.t1, tuple.t2.getName());

                    return c.stream()
                            .limit(limit)
                            .map(commit -> new Tuple3<String, String, GitHub.Commit>(tuple.t1, tuple.t2.getName(), commit));
                })
                .map(tuple -> gitHub.commit(tuple.t1, tuple.t2, tuple.t3.getSha()))
                .collect(toList());

        assertThat(commits).isNotEmpty();
    }

    @RequiredArgsConstructor
    private static class Tuple2<T1, T2> {
        private final T1 t1;
        private final T2 t2;
    }

    @RequiredArgsConstructor
    private static class Tuple3<T1, T2, T3> {
        private final T1 t1;
        private final T2 t2;
        private final T3 t3;
    }
}