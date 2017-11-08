package org.abhijitsarkar.spring.beer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

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

    @Autowired
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private ConfigurableEnvironment env;

    public void setNodes(List<String> nodes) {
        if (nonNull(env) && !isEmpty(nodes)) {
            this.nodes = nodes.stream()
                    .map(env::resolvePlaceholders)
                    .collect(toList());
        }
    }

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
