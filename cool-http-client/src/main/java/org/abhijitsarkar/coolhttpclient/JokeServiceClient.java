package org.abhijitsarkar.coolhttpclient;

import com.netflix.hystrix.HystrixCommand;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Abhijit Sarkar
 */
// Hystrix failover doesn't work for HystrixCommand return types
@FeignClient(name = "joke-service")
public interface JokeServiceClient {
    public static final String RANDOM_JOKE_URI = "/jokes/random";

    @RequestMapping(RANDOM_JOKE_URI)
    HystrixCommand<JokeServiceResponse> tellAJoke();
}
