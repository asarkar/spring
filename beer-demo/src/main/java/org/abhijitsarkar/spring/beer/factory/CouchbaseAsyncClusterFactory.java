package org.abhijitsarkar.spring.beer.factory;

import com.couchbase.client.java.AsyncCluster;
import com.couchbase.client.java.CouchbaseAsyncCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.spring.beer.CouchbaseProperties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import rx.Single;
import rx.SingleSubscriber;

import java.util.List;
import java.util.function.BiFunction;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Abhijit Sarkar
 */
public interface CouchbaseAsyncClusterFactory {
    static CouchbaseAsyncClusterFactory newInstance(
            CouchbaseEnvironment environment,
            CouchbaseProperties couchbaseProperties) {
        return new DefaultCouchbaseAsyncClusterFactory(environment, couchbaseProperties);
    }

    Single<AsyncCluster> getAsyncClusterInstance();

    @Slf4j
    class DefaultCouchbaseAsyncClusterFactory implements CouchbaseAsyncClusterFactory, InitializingBean, DisposableBean {
        final CouchbaseEnvironment environment;
        final CouchbaseProperties couchbaseProperties;

        Single<AsyncCluster> asyncCluster;

        // indirection for testing
        BiFunction<CouchbaseEnvironment, List<String>, CouchbaseAsyncCluster> clusterCreator =
                CouchbaseAsyncCluster::create;

        DefaultCouchbaseAsyncClusterFactory(
                CouchbaseEnvironment environment,
                CouchbaseProperties couchbaseProperties
        ) {
            requireNonNull(couchbaseProperties, "CouchbaseProperties must not be null.");
            this.environment = isNull(environment) ? DefaultCouchbaseEnvironment
                    .builder()
                    .build() : environment;
            this.couchbaseProperties = couchbaseProperties;
        }

        @Override
        public Single<AsyncCluster> getAsyncClusterInstance() {
            return asyncCluster;
        }

        boolean disconnect() {
            List<String> nodes = couchbaseProperties.getNodes();
            log.info("Disconnecting from Couchbase cluster: {}.", nodes);

            return asyncCluster
                    .flatMapObservable(AsyncCluster::disconnect)
                    .toCompletable()
                    .doOnError(t -> log.error("Failed to disconnect from Couchbase cluster: {}.", nodes, t))
                    .await(couchbaseProperties.getClusterDisconnectTimeoutMillis(), MILLISECONDS);
        }

        final Single<AsyncCluster> newAsyncClusterInstance() {
            return Single.create((SingleSubscriber<? super AsyncCluster> subscriber) -> {
                List<String> nodes = couchbaseProperties.getNodes();
                try {
                    log.info("Connecting to Couchbase cluster: {}.", nodes);
                    AsyncCluster asyncCluster = clusterCreator.apply(environment, nodes);
                    subscriber.onSuccess(asyncCluster);
                } catch (Exception e) {
                    log.error("Failed to connect to Couchbase cluster: {}.", nodes, e);
                    subscriber.onError(e);
                }
            })
                    .timeout(couchbaseProperties.getClusterConnectTimeoutMillis(), MILLISECONDS);
        }

        @Override
        public void destroy() throws Exception {
            disconnect();
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            asyncCluster = newAsyncClusterInstance()
                    .flatMap(cluster -> Single.just(cluster).cache());
        }
    }
}
