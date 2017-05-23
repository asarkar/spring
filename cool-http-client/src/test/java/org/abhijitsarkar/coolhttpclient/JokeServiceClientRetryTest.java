package org.abhijitsarkar.coolhttpclient;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.VerificationResult;
import org.assertj.core.api.Condition;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.serviceUnavailable;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.abhijitsarkar.coolhttpclient.JokeServiceClient.RANDOM_JOKE_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Index.atIndex;
import static org.springframework.util.StreamUtils.copyToByteArray;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "joke-service.ribbon.listOfServers=localhost:22222",
                "joke-service.ribbon.retryableStatusCodes=503",
                "joke-service.ribbon.MaxAutoRetries=2",
                "hystrix.command.JokeServiceClient#tellAJoke().execution.isolation.thread.timeoutInMilliseconds=10000"
        })
@DirtiesContext // adds properties, tell Spring not to cache context
public class JokeServiceClientRetryTest {
    @Autowired
    private JokeServiceClient client;

    @Rule
    public WireMockRule server = new WireMockRule(options().port(22222).notifier(new ConsoleNotifier(true)));

    @Test
    public void testRetryUsingSpringRetry() throws IOException {
        server.stubFor(get(urlPathEqualTo(RANDOM_JOKE_URI))
                .inScenario("Feign retry")
                .whenScenarioStateIs(STARTED)
                .willReturn(serviceUnavailable())
                .willSetStateTo("1"));

        server.stubFor(get(urlPathEqualTo(RANDOM_JOKE_URI))
                .inScenario("Feign retry")
                .whenScenarioStateIs("1")
                .willReturn(serviceUnavailable())
                .willSetStateTo("2"));

        server.stubFor(get(urlPathEqualTo(RANDOM_JOKE_URI))
                .inScenario("Feign retry")
                .whenScenarioStateIs("2")
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(copyToByteArray(getClass().getResourceAsStream("/joke.json")))));

        TestSubscriber<JokeServiceResponse> subscriber = new TestSubscriber<>();
        // GOTCHA ALERT: must use defer for the Observable to be reevaluated
        Observable.defer(() -> client.tellAJoke()
                .toObservable())
                .subscribe(subscriber);

        subscriber.awaitTerminalEvent(10, TimeUnit.SECONDS);

        VerificationResult numRequests = server
                .countRequestsMatching(getRequestedFor(urlPathEqualTo(RANDOM_JOKE_URI)).build());

        assertThat(numRequests.getCount()).isEqualTo(3);
        subscriber.assertNoErrors();
        assertThat(subscriber.getOnNextEvents())
                .hasSize(1)
                .is(new Condition<JokeServiceResponse>() {
                    @Override
                    public boolean matches(JokeServiceResponse response) {
                        assertResponse(response);
                        return true;
                    }
                }, atIndex(0));
    }

    private void assertResponse(JokeServiceResponse response) {
        assertThat(response)
                .isNotNull()
                .hasFieldOrPropertyWithValue("status", "success");
        assertThat(response.getJoke())
                .isNotEqualTo(Optional.empty());
        assertThat(response.getJoke().get())
                .is(new Condition<JokeServiceResponse.Joke>() {
                    @Override
                    public boolean matches(JokeServiceResponse.Joke joke) {
                        assertThat(joke.getText())
                                .isNotBlank();
                        assertThat(joke.getId())
                                .isPositive();
                        return true;
                    }
                });
    }
}
