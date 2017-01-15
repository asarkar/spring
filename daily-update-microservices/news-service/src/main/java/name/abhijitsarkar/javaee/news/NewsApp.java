package name.abhijitsarkar.javaee.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import name.abhijitsarkar.javaee.common.CommonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter.DEFAULT_CHARSET;

/**
 * @author Abhijit Sarkar
 */
@Configuration
@EnableAutoConfiguration
@EnableFeignClients
@EnableDiscoveryClient
@EnableCaching
@ComponentScan(basePackageClasses = {CommonConfig.class, NewsApp.class})
public class NewsApp {
    @Autowired
    private ObjectMapper objectMapper;

    public static void main(String[] args) {
        SpringApplication.run(NewsApp.class, args);
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter messageConverter =
                new MappingJackson2HttpMessageConverter(objectMapper);

        /* NYT returns text/json */
        List<MediaType> supportedMediaTypes = ImmutableList.of(
                new MediaType("text", "json"),
                APPLICATION_JSON_UTF8,
                new MediaType("application", "*+json", DEFAULT_CHARSET));

        messageConverter.setSupportedMediaTypes(supportedMediaTypes);

        return messageConverter;
    }
}
