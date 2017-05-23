package org.abhijitsarkar.coolhttpclient;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.cloud.netflix.feign.ribbon.FeignRibbonClientAutoConfiguration;
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
    OkHttpClient okhttpClient() {
        ConnectionPool connectionPool = new ConnectionPool(
                clientProperties.getMaxIdleConnections(), clientProperties.getKeepAliveMillis(), TimeUnit.MILLISECONDS);

        return new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .connectTimeout(clientProperties.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(clientProperties.getReadTimeoutMillis(), TimeUnit.MILLISECONDS)
                .followRedirects(clientProperties.isFollowRedirects())
                .build();
    }
}
