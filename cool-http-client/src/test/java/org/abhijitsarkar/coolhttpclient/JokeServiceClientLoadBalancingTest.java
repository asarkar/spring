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

import java.io.IOException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.serviceUnavailable;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.abhijitsarkar.coolhttpclient.JokeServiceClient.RANDOM_JOKE_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.StreamUtils.copyToByteArray;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "joke-service.ribbon.listOfServers=localhost:11111,localhost:11112"
        })
@DirtiesContext // adds properties, tell Spring not to cache context
public class JokeServiceClientLoadBalancingTest {
    @Rule
    public WireMockRule goodServer = new WireMockRule(options().port(11111).notifier(new ConsoleNotifier(true)));

    @Rule
    public WireMockRule badServer = new WireMockRule(options().port(11112).notifier(new ConsoleNotifier(true)));

    @Autowired
    private JokeServiceClient client;

    @Test
    public void testLoadBalancing() throws IOException {
        badServer.stubFor(get(urlPathEqualTo(RANDOM_JOKE_URI))
                .willReturn(serviceUnavailable()));
        goodServer.stubFor(get(urlPathEqualTo(RANDOM_JOKE_URI))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(copyToByteArray(getClass().getResourceAsStream("/joke.json")))));

        // GOTCHA ALERT: must use defer for the Observable to be reevaluated
        List<JokeServiceResponse> responses = Observable.range(1, 5)
                .flatMap(i -> Observable.defer(() -> client.tellAJoke()
                        .toObservable())
                        // Don't let an error terminate the main stream
                        .onErrorResumeNext(Observable.just(new JokeServiceResponse())))
                .toList()
                .toBlocking()
                .first();

        assertThat(responses)
                .haveExactly(3, new Condition<JokeServiceResponse>() {
                    @Override
                    public boolean matches(JokeServiceResponse response) {
                        return response.getStatus().equalsIgnoreCase("success");
                    }
                })
                .haveExactly(2, new Condition<JokeServiceResponse>() {
                    @Override
                    public boolean matches(JokeServiceResponse response) {
                        return response.getStatus().equalsIgnoreCase("failure");
                    }
                });

        VerificationResult numGoodRequests = goodServer
                .countRequestsMatching(getRequestedFor(urlPathEqualTo(RANDOM_JOKE_URI)).build());
        VerificationResult numBadRequests = badServer
                .countRequestsMatching(getRequestedFor(urlPathEqualTo(RANDOM_JOKE_URI)).build());

        assertThat(numGoodRequests.getCount()).isEqualTo(3);
        assertThat(numBadRequests.getCount()).isEqualTo(2);
    }
}
