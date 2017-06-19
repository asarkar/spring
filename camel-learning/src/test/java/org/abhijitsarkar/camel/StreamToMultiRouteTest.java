package org.abhijitsarkar.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.abhijitsarkar.camel.Application.DEFAULT_PROFILE;
import static org.abhijitsarkar.camel.Application.OUTBOUND_HTTP_PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.NottableString.string;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

/**
 * @author Abhijit Sarkar
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@ActiveProfiles({DEFAULT_PROFILE, OUTBOUND_HTTP_PROFILE})
public class StreamToMultiRouteTest {
    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private Supplier<OutputStream> consumer;

    @Autowired
    private CamelContext camelContext;

    @Rule
    public MockServerRule mockServer = new MockServerRule(this, 22222);

    private MockServerClient mockServerClient;

    @After
    public void afterEach() {
        ((Application.ByteArrayOutputStreamConsumer) consumer).reset();
        mockServerClient.reset();
    }

    @Test
    public void testMultiRoute() throws IOException {
        NotifyBuilder notify = new NotifyBuilder(camelContext)
                .whenDone(3)
                .create();

        URL url = new URL(String.format("http://localhost:%d/test", mockServer.getPort()));

        Map<String, Object> headers = new HashMap<>();
        headers.put(Exchange.FILE_NAME, "test.out");
        headers.put(Exchange.HTTP_URI, url.toString());

        mockServerClient.when(
                request()
                        .withMethod(PUT.name())
                        .withPath(url.getPath())
        )
                .respond(response()
                        .withStatusCode(OK.value())
                );

        producerTemplate.sendBodyAndHeaders("direct:in", "hi", headers);

        assertThat(notify.matches(5, TimeUnit.SECONDS));

        mockServerClient.verify(
                request()
                        .withMethod(PUT.name())
                        .withPath(url.getPath())
                        .withHeader(CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE)
                        .withHeader(ACCEPT, APPLICATION_JSON_VALUE)
                        .withHeader(string(AUTHORIZATION), string(".+")),
                VerificationTimes.once()
        );

        String body = ((Application.ByteArrayOutputStreamConsumer) consumer).getBos()
                .toString(UTF_8.name());

        assertThat(body).isEqualTo("hi");
        Path out = Paths.get("build", "test.out");
        assertThat(Files.exists(out));

        String content = new String(Files.readAllBytes(out), UTF_8);
        assertThat(content).isEqualTo("hi");
    }
}