package org.abhijitsarkar.spring.beer;

import com.couchbase.client.core.metrics.DefaultLatencyMetricsCollectorConfig;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.spring.beer.factory.CouchbaseAsyncBucketFactory;
import org.abhijitsarkar.spring.beer.factory.CouchbaseAsyncClusterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.couchbase.CouchbaseRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(CouchbaseProperties.class)
@EnableAutoConfiguration(exclude = {CouchbaseRepositoriesAutoConfiguration.class})
@Slf4j
// Wait until all CB properties have been resolved
@Lazy
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.ExcessivePublicCount"})
public class CouchbaseConfiguration {
    @Autowired
    @Setter(AccessLevel.PACKAGE)
    private CouchbaseProperties couchbaseProperties;

    @Bean
    CouchbaseEnvironment couchbaseEnvironment() {
        DefaultLatencyMetricsCollectorConfig.Builder builder = DefaultLatencyMetricsCollectorConfig.builder()
                .emitFrequency(5)
                .emitFrequencyUnit(TimeUnit.SECONDS)
                .targetUnit(TimeUnit.MILLISECONDS);

        return DefaultCouchbaseEnvironment.builder()
                .networkLatencyMetricsCollectorConfig(builder.build())
                .dnsSrvEnabled(couchbaseProperties.isDnsSrvEnabled())
                .build();
    }

    @Bean
    CouchbaseAsyncClusterFactory couchbaseAsyncClusterFactory() {
        return CouchbaseAsyncClusterFactory.newInstance(couchbaseEnvironment(), couchbaseProperties);
    }

    @Bean
    CouchbaseAsyncBucketFactory couchbaseAsyncBucketFactory() {
        return CouchbaseAsyncBucketFactory.newInstance(couchbaseAsyncClusterFactory(), couchbaseProperties);
    }
}
