package name.abhijitsarkar.javaee.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author Abhijit Sarkar
 */
@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigServer {
    /* Check out the EnvironmentRepositoryConfiguration for details */
    public static void main(String[] args) {
        SpringApplication.run(ConfigServer.class, args);
    }
}
