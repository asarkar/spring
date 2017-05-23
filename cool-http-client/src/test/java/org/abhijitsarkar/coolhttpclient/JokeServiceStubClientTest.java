package org.abhijitsarkar.coolhttpclient;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

/**
 * @author Abhijit Sarkar
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "hystrix.command.joke-svc-cmd.execution.isolation.thread.timeoutInMilliseconds=10"
        })
@DirtiesContext
// adds properties, tell Spring not to cache context
public class JokeServiceStubClientTest {
    @Autowired
    private JokeServiceStubClient client;

    @Test
    public void testHystrixCommandTimeout() {
        assertThatExceptionOfType(HystrixRuntimeException.class).isThrownBy(() -> client.tellAJoke());
    }

    @Configuration
    @ComponentScan
    // GOTCHA ALERT: Need to explicitly enable circuit breaker (Hystrix) for @HystrixCommand to work
    @EnableHystrix // or @EnableCircuitBreaker
    static class JokeServiceStubClientConfiguration {
    }
}