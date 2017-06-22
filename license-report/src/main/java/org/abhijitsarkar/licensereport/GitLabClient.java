package org.abhijitsarkar.licensereport;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;

/**
 * @author Abhijit Sarkar
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class GitLabClient {
    private final GitLabProperties gitLabProperties;

    Flux<Tuple2<Project, String>> getProjects(GitLabProperties.GroupProperties group) {
        return Mono.<Group>create(consumer -> {
            CloseableHttpClient client = newHttpClient();
            CloseableHttpResponse response = null;

            log.info("Fetching group: {}.", group.getName());

            try {
                HttpGet httpGet = new HttpGet(String.format("%s/api/v4/groups/%s",
                        gitLabProperties.getBaseUrl(), group.getName()));
                httpGet.addHeader("PRIVATE-TOKEN", group.getPrivateToken());
                httpGet.addHeader(ACCEPT, "application/json");
                response = client.execute(httpGet);

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode < SC_BAD_REQUEST) {
                    log.debug("Received response code: {} when retrieving group: {}.",
                            statusCode, group.getName());

                    Group grp = new ObjectMapper().readValue(response.getEntity().getContent(),
                            Group.class);

                    consumer.success(grp);
                } else {
                    consumer.error(new RuntimeException(
                            String.format("Received response code: %d when retrieving group: %s.",
                                    statusCode, group.getName())));
                }
            } catch (IOException e) {
                consumer.error(e);
            } finally {
                closeOrEmitError(consumer, response, client);
            }
        })
                .flatMapIterable(Group::getProjects)
                .map(p -> Tuples.of(p, group.getName()));
    }

    private final void closeOrEmitError(MonoSink<Group> consumer, Closeable... resources) {
        List<IOException> errors = new ArrayList<>();

        for (Closeable r : resources) {
            try {
                r.close();
            } catch (IOException e) {
                errors.add(e);
            }
        }

        if (!errors.isEmpty()) {
            log.error("Failed to close connection!");

            errors.stream()
                    .forEach(e -> log.error(e.getMessage(), e));

            consumer.error(errors.get(0));
        }
    }

    private final CloseableHttpClient newHttpClient() {
        GitLabProperties.ConnectionProperties connection = gitLabProperties.getConnection();

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(connection.getReadTimeoutMillis())
                .setConnectTimeout(connection.getConnectTimeoutMillis())
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

    }
}
