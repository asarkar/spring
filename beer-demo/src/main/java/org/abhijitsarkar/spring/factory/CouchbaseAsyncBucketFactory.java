package org.abhijitsarkar.spring.factory;

import com.couchbase.client.core.lang.Tuple;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.AsyncCluster;
import com.couchbase.client.java.cluster.AsyncClusterManager;
import com.couchbase.client.java.cluster.DefaultBucketSettings;
import com.couchbase.client.java.query.Index;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.SimpleN1qlQuery;
import com.couchbase.client.java.query.Statement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.spring.CouchbaseProperties;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import javax.annotation.PostConstruct;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.abhijitsarkar.spring.CouchbaseQueryUtil.executeN1qlQuery;

/**
 * @author Abhijit Sarkar
 */
@RequiredArgsConstructor
@Slf4j
public class CouchbaseAsyncBucketFactory {
    private final CouchbaseAsyncClusterFactory couchbaseAsyncClusterFactory;
    private final CouchbaseProperties couchbaseProperties;

    private Single<AsyncBucket> asyncBucket;

    @PostConstruct
    void init() {
        asyncBucket = newInstance().cache();
    }

    public Single<AsyncBucket> getInstance() {
        return asyncBucket;
    }

    public void close() {
        log.debug("Closing bucket: {}.", couchbaseProperties.getBucket().getName());

        asyncBucket
                .flatMapObservable(AsyncBucket::close)
                .toCompletable()
                .await(couchbaseProperties.getBucket().getBucketCloseTimeoutMillis(), MILLISECONDS);
    }

    private final Single<AsyncBucket> newInstance() {
        CouchbaseProperties.BucketProperties bucket = couchbaseProperties.getBucket();

        return couchbaseAsyncClusterFactory.getInstance()
                .flatMapObservable(cluster -> cluster.clusterManager(
                        couchbaseProperties.getAdminUsername(),
                        couchbaseProperties.getAdminPassword())
                        .map(clusterManager -> Tuple.create(cluster, clusterManager))
                )
                .flatMap(tuple -> {
                    AsyncCluster cluster = tuple.value1();
                    AsyncClusterManager clusterManager = tuple.value2();

                    return clusterManager.hasBucket(bucket.getName())
                            .map(hasBucket -> {
                                log.debug("Bucket: {} is: {}.", bucket.getName(),
                                        hasBucket ? "already present" : "absent");
                                return !hasBucket && bucket.isCreateIfMissing();
                            })
                            .filter(Boolean::booleanValue)
                            .flatMap(x -> {
                                log.debug("Creating bucket: {}.", bucket.getName());
                                return clusterManager.insertBucket(
                                        DefaultBucketSettings.builder()
                                                .name(bucket.getName())
                                                .password(bucket.getPassword())
                                                .enableFlush(bucket.isEnableFlush())
                                                .quota(bucket.getDefaultQuotaMB())
                                                .build());
                            })
                            .flatMap(x -> cluster.openBucket(bucket.getName(), bucket.getPassword())
                                    .timeout(bucket.getBucketOpenTimeoutMillis(), MILLISECONDS))
                            .map(b -> {
                                Statement statement = Index.createPrimaryIndex()
                                        .on(bucket.getName());
                                SimpleN1qlQuery query = N1qlQuery.simple(statement);

                                // create index on separate thread, but wait first for bucket to be opened
                                Observable.just(1)
                                        .observeOn(Schedulers.io())
                                        .flatMap(i ->
                                                Observable.timer(bucket.getBucketOpenTimeoutMillis(), MILLISECONDS)
                                                        .zipWith(executeN1qlQuery(b, query), (j, row) -> row))
                                        .toCompletable()
                                        .doOnTerminate(() -> log.debug("Subscribed on thread: {}.",
                                                Thread.currentThread().getName()))
                                        .await(bucket.getBucketOpenTimeoutMillis(), MILLISECONDS);

                                return b;
                            })
                            .switchIfEmpty(cluster.openBucket(bucket.getName(), bucket.getPassword())
                                    .timeout(bucket.getBucketOpenTimeoutMillis(), MILLISECONDS));
                })
                .toSingle();
    }
}
