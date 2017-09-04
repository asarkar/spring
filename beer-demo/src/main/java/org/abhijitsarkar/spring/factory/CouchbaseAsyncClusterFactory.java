package org.abhijitsarkar.spring.factory;

import com.couchbase.client.java.AsyncCluster;
import com.couchbase.client.java.CouchbaseAsyncCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.spring.CouchbaseProperties;
import rx.Single;
import rx.SingleSubscriber;

import javax.annotation.PostConstruct;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
public class CouchbaseAsyncClusterFactory {
    private CouchbaseEnvironment environment;
    private List<String> nodes;
    private CouchbaseProperties couchbaseProperties;

    private Single<AsyncCluster> asyncCluster;

    @Builder
    public CouchbaseAsyncClusterFactory(
            CouchbaseEnvironment environment,
            List<String> nodes,
            CouchbaseProperties couchbaseProperties
    ) {
        requireNonNull(couchbaseProperties, "CouchbaseProperties must not be null");
        this.environment = isNull(environment) ? DefaultCouchbaseEnvironment
                .builder()
                .build() : environment;
        this.nodes = isNull(nodes) ? asList("localhost") : nodes;
        this.couchbaseProperties = couchbaseProperties;
    }

    @PostConstruct
    void init() {
        asyncCluster = newInstance().cache();
    }

    public Single<AsyncCluster> getInstance() {
        return asyncCluster;
    }

    public void disconnect() {
        log.info("Disconnecting from Couchbase cluster: {}.", nodes);
        asyncCluster
                .flatMapObservable(AsyncCluster::disconnect)
                .toCompletable()
                .doOnError(t -> log.error("Failed to disconnect from Couchbase cluster: {}.", nodes, t))
                .await(couchbaseProperties.getClusterDisconnectTimeoutMillis(), MILLISECONDS);
    }

    private final Single<AsyncCluster> newInstance() {
        return Single.create((SingleSubscriber<? super AsyncCluster> subscriber) -> {
            try {
                log.info("Connecting to Couchbase cluster: {}.", nodes);
                AsyncCluster asyncCluster = CouchbaseAsyncCluster.create(environment, nodes);
                subscriber.onSuccess(asyncCluster);
            } catch (Exception e) {
                log.error("Failed to connect to Couchbase cluster: {}.", nodes, e);
                subscriber.onError(e);
            }
        })
                .timeout(couchbaseProperties.getClusterConnectTimeoutMillis(), MILLISECONDS);
    }
}
