package org.abhijitsarkar.spring.beer;

import com.couchbase.client.core.metrics.DefaultLatencyMetricsCollectorConfig;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import lombok.AccessLevel;
import lombok.Setter;
import org.abhijitsarkar.spring.beer.factory.CouchbaseAsyncBucketFactory;
import org.abhijitsarkar.spring.beer.factory.CouchbaseAsyncClusterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author Abhijit Sarkar
 */
@Configuration
@EnableConfigurationProperties(CouchbaseProperties.class)
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

    @Bean
    DbInitializer dbInitializer(CouchbaseAsyncBucketFactory couchbaseAsyncBucketFactory) {
        return new DbInitializer(couchbaseAsyncBucketFactory);
    }
}