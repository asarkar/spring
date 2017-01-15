package name.abhijitsarkar.javaee.weather;

import name.abhijitsarkar.javaee.common.CommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Abhijit Sarkar
 */
@Configuration
@EnableAutoConfiguration
@EnableFeignClients
@EnableDiscoveryClient
@EnableCaching
@ComponentScan(basePackageClasses = {WeatherApp.class, CommonConfig.class})
public class WeatherApp {
    public static void main(String[] args) {
        SpringApplication.run(WeatherApp.class, args);
    }
}
