package org.abhijitsarkar.spring.beer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static java.util.Arrays.asList;

@Data
@ConfigurationProperties(prefix = "couchbase")
@SuppressWarnings({"PMD.ExcessivePublicCount", "PMD.ImmutableField"})
public class CouchbaseProperties {
    private List<String> nodes = asList("localhost");
    private String adminUsername;
    private String adminPassword = "";
    private BucketProperties bucket;
    private long clusterOperationTimeoutMillis = 5000;
    private long clusterDisconnectTimeoutMillis = 25000;
    private long blockingOperationTimeoutMillis = 5000;
    private boolean dnsSrvEnabled = true;

    @Data
    public static class BucketProperties {
        private int defaultQuotaMB = 100;
        private boolean indexReplicas;
        private boolean enableFlush = true;
        private boolean createIfMissing;
        private String name;
        private long bucketCreateTimeoutMillis = 10000;
        private long bucketOpenTimeoutMillis = bucketCreateTimeoutMillis;
        private long bucketCloseTimeoutMillis = 25000;
        private long sleepWindowMillis = 5 * bucketOpenTimeoutMillis;
    }
}
