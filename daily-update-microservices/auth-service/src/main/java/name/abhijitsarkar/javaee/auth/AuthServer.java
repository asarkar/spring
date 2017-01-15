package name.abhijitsarkar.javaee.auth;

import name.abhijitsarkar.javaee.common.CommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author Abhijit Sarkar
 */
@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
@RestController
@ComponentScan(basePackageClasses = {AuthServer.class, CommonConfig.class})
public class AuthServer {
    public static void main(String[] args) {
        SpringApplication.run(AuthServer.class, args);
    }

    @RequestMapping("/**")
    public Principal user(Principal user) {
        return user;
    }

}
