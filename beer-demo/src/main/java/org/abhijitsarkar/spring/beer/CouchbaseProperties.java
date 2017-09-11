package org.abhijitsarkar.spring.beer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Abhijit Sarkar
 */
@ConfigurationProperties("couchbase")
@Data
public class CouchbaseProperties {
    private List<String> nodes = asList("localhost");
    private String adminUsername;
    private String adminPassword;
    private BucketProperties bucket;
    private long clusterConnectTimeoutMillis = 5000;
    private long clusterDisconnectTimeoutMillis = clusterConnectTimeoutMillis;

    @Data
    public static class BucketProperties {
        int defaultQuotaMB = 100;
        boolean enableFlush = true;
        boolean createIfMissing = true;
        private boolean indexReplicas;
        private String name;
        private String password = "";
        private long bucketOpenTimeoutMillis = 15000;
        private long bucketCloseTimeoutMillis = bucketOpenTimeoutMillis;
    }
}
