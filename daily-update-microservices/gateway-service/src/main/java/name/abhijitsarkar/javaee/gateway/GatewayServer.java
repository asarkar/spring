package name.abhijitsarkar.javaee.gateway;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author Abhijit Sarkar
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
public class GatewayServer {
    public static void main(String[] args) {
        new SpringApplicationBuilder(GatewayServer.class).web(true).run(args);
    }
}
