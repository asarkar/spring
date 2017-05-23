package org.abhijitsarkar.coolhttpclient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Component;

/**
 * @author Abhijit Sarkar
 */
@Component
public class JokeServiceStubClient {
    @HystrixCommand(groupKey = "joke-svc-broker", commandKey = "joke-svc-cmd", threadPoolKey = "joke-svc-pool")
    public String tellAJoke() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }
}
