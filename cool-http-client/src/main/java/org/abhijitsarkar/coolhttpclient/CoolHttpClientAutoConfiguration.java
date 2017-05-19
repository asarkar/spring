package org.abhijitsarkar.coolhttpclient;

import feign.Client;
import feign.okhttp.OkHttpClient;
import okhttp3.ConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.cloud.netflix.feign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.netflix.feign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.cloud.netflix.feign.ribbon.LoadBalancerFeignClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * @author Abhijit Sarkar
 */
@SpringBootApplication
@EnableFeignClients
@AutoConfigureBefore({FeignAutoConfiguration.class, FeignRibbonClientAutoConfiguration.class})
public class CoolHttpClientAutoConfiguration {
    @Autowired
    private CoolHttpClientProperties clientProperties;

    @Bean
    Client okhttpClient(CachingSpringLoadBalancerFactory cachingFactory,
                        SpringClientFactory clientFactory) {
        ConnectionPool connectionPool = new ConnectionPool(
                clientProperties.getMaxIdleConnections(), clientProperties.getKeepAliveMillis(), TimeUnit.MILLISECONDS);

        okhttp3.OkHttpClient delegate = new okhttp3.OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .connectTimeout(clientProperties.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(clientProperties.getReadTimeoutMillis(), TimeUnit.MILLISECONDS)
                .followRedirects(clientProperties.isFollowRedirects())
                .build();

        return new LoadBalancerFeignClient(new OkHttpClient(delegate), cachingFactory, clientFactory);
    }
}
