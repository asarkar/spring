package org.abhijitsarkar.camel;

import org.abhijitsarkar.camel.http.HttpProperties;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.After;
import org.junit.Ignore;
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
import java.util.concurrent.TimeUnit;

import static org.abhijitsarkar.camel.Application.INBOUND_S3_PROFILE;
import static org.abhijitsarkar.camel.Application.OUTBOUND_HTTP_PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author Abhijit Sarkar
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@ActiveProfiles({INBOUND_S3_PROFILE, OUTBOUND_HTTP_PROFILE})
@Ignore("Needs S3 account")
public class S3ToDynamicRouteTest {
    @Autowired
    private CamelContext camelContext;

    @Autowired
    private HttpProperties httpProperties;

    @Rule
    public MockServerRule mockServer = new MockServerRule(this, 22222);

    private MockServerClient mockServerClient;

    @After
    public void afterEach() {
        mockServerClient.reset();
    }

    @Test
    public void testS3ToHttpRoute() throws IOException {
        NotifyBuilder notify = new NotifyBuilder(camelContext)
                .whenDone(1)
                .create();

        mockServerClient.when(
                request()
                        .withMethod(PUT.name())
                        .withPath(httpProperties.getPath())
        )
                .respond(response()
                        .withStatusCode(OK.value())
                );

        assertThat(notify.matches(5, TimeUnit.SECONDS));

        mockServerClient.verify(
                request()
                        .withMethod(PUT.name())
                        .withPath(httpProperties.getPath()),
                VerificationTimes.once()
        );
    }
}