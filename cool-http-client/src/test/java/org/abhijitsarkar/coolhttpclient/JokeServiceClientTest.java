package org.abhijitsarkar.coolhttpclient;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.assertj.core.api.Condition;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import rx.Observable;
import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.abhijitsarkar.coolhttpclient.JokeServiceClient.RANDOM_JOKE_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Index.atIndex;
import static org.springframework.util.StreamUtils.copyToByteArray;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "joke-service.ribbon.listOfServers=localhost:11111"
        })
@DirtiesContext // adds properties, tell Spring not to cache context
public class JokeServiceClientTest {
    @Autowired
    private JokeServiceClient client;

    @Rule
    public WireMockRule server = new WireMockRule(11111);

    @Test
    public void testLive() {
        server.stubFor(get(urlPathEqualTo(RANDOM_JOKE_URI))
                .willReturn(aResponse().proxiedFrom("http://api.icndb.com")));

        TestSubscriber<JokeServiceResponse> subscriber = new TestSubscriber<>();
        client.tellAJoke()
                .toObservable()
                .subscribe(subscriber);

        subscriber.awaitTerminalEvent(3, TimeUnit.SECONDS);
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

    @Test
    public void testRetry() throws IOException {
        server.stubFor(get(urlPathEqualTo(RANDOM_JOKE_URI))
                .inScenario("Feign retry")
                .whenScenarioStateIs(STARTED)
                .willReturn(badRequest())
                .willSetStateTo("1"));

        server.stubFor(get(urlPathEqualTo(RANDOM_JOKE_URI))
                .inScenario("Feign retry")
                .whenScenarioStateIs("1")
                .willReturn(badRequest())
                .willSetStateTo("2"));

        server.stubFor(get(urlPathEqualTo(RANDOM_JOKE_URI))
                .inScenario("Feign retry")
                .whenScenarioStateIs("2")
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(copyToByteArray(getClass().getResourceAsStream("/joke.json")))));

        TestSubscriber<JokeServiceResponse> subscriber = new TestSubscriber<>();
        // TestScheduler virtual time doesn't work if a real wait (WireMock) is involved
//        TestScheduler scheduler = new TestScheduler();
        // GOTCHA ALERT: must use defer for the Observable to be reevaluated
        Observable.defer(() -> client.tellAJoke()
                .toObservable())
                .retryWhen(errors -> onErrorTryAgain(errors, Schedulers.computation()))
                .subscribe(subscriber);

        subscriber.awaitTerminalEvent(10, TimeUnit.SECONDS);
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

    private Observable<?> onErrorTryAgain(Observable<? extends Throwable> errors, Scheduler scheduler) {
        return errors
                .zipWith(Observable.range(1, 5), (t, i) -> new SimpleImmutableEntry<Integer, Throwable>(i, t))
                .flatMap(e -> {
                    // Can inspect Throwable here and decide whether to retry or return Observable.error
                    return Observable.timer((long) Math.pow(2, e.getKey()),
                            TimeUnit.SECONDS, scheduler);
                });
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
