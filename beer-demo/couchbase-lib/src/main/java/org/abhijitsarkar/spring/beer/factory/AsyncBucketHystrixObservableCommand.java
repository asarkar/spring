package org.abhijitsarkar.spring.beer.factory;

import com.couchbase.client.core.lang.Tuple;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.AsyncCluster;
import com.couchbase.client.java.cluster.AsyncClusterManager;
import com.couchbase.client.java.cluster.BucketSettings;
import com.couchbase.client.java.cluster.DefaultBucketSettings;
import com.couchbase.client.java.query.AsyncN1qlQueryRow;
import com.couchbase.client.java.query.Index;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.SimpleN1qlQuery;
import com.couchbase.client.java.query.Statement;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.spring.beer.CouchbaseProperties;
import org.abhijitsarkar.spring.beer.CouchbaseQueryUtil;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.function.BiFunction;

import static com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
public class AsyncBucketHystrixObservableCommand extends HystrixObservableCommand<AsyncBucket> {
    final CouchbaseAsyncClusterFactory couchbaseAsyncClusterFactory;
    final CouchbaseProperties couchbaseProperties;

    Scheduler scheduler = Schedulers.io();

    // indirections for testing
    BiFunction<AsyncBucket, N1qlQuery, Observable<AsyncN1qlQueryRow>> queryExecutor =
            CouchbaseQueryUtil::executeN1qlQuery;

    AsyncBucketHystrixObservableCommand(
            CouchbaseAsyncClusterFactory couchbaseAsyncClusterFactory,
            CouchbaseProperties couchbaseProperties,
            Setter setter
    ) {
        super(setter);

        requireNonNull(couchbaseProperties.getAdminUsername(), "Admin username must not be null.");

        this.couchbaseAsyncClusterFactory = couchbaseAsyncClusterFactory;
        this.couchbaseProperties = couchbaseProperties;
    }

    public AsyncBucketHystrixObservableCommand(
            CouchbaseAsyncClusterFactory couchbaseAsyncClusterFactory,
            CouchbaseProperties couchbaseProperties
    ) {
        this(couchbaseAsyncClusterFactory, couchbaseProperties, commandSetter(couchbaseProperties.getBucket()));
    }

    static Setter commandSetter(CouchbaseProperties.BucketProperties bucket) {
        requireNonNull(bucket, "BucketProperties must not be null.");
        requireNonNull(bucket.getName(), "Bucket name must not be null.");

        return HystrixObservableCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(bucket.getName() + "-grp"))
                .andCommandKey(HystrixCommandKey.Factory.asKey(bucket.getName() + "-cmd"))
                .andCommandPropertiesDefaults(commandPropertiesSetter(bucket));
    }

    static HystrixCommandProperties.Setter commandPropertiesSetter(CouchbaseProperties.BucketProperties bucket) {
        return HystrixCommandProperties.Setter()
                // This is the rolling window duration; name is misleading, it's used for metrics as
                // well as the circuit breaker.
//                                .withMetricsRollingStatisticalWindowInMilliseconds(10000)
                // This is how long the circuit will stay open before a single request is let through.
                .withCircuitBreakerSleepWindowInMilliseconds((int) bucket.getSleepWindowMillis())
                // This sets the minimum number of requests in a rolling window that will trip the circuit.
                .withCircuitBreakerRequestVolumeThreshold(1)
                // This sets the error percentage at or above which the circuit should trip open;
                // setting this to zero forces any error o trip the circuit.
                .withCircuitBreakerErrorThresholdPercentage(0)
                // https://github.com/Netflix/Hystrix/issues/805
                /*
                 Even though it is possible, it's generally inappropriate for a HystrixObservableCommand
                 to be using a thread since that kind of defeats the point of avoiding threads
                 and wrapping non-blocking Observables.
                 HystrixObservableCommand is for wrapping non-blocking Observables.
                 If the Observable is blocking, then it is questionable as to why an Observable is
                 being used and thus HystrixCommand is probably the right thing since it is blocking.
                 If there is a legit reason for the Observable to be blocking,
                 and subscribeOn(Schedulers.io()) is not the right solution, then yes,
                 Hystrix threads can be used to make it async. But that's not the intended pattern.

                 If you already have a non-blocking Observable, you don't need another thread.
                 It's wasteful as all that will happen is it will schedule the thread to schedule
                 the async Observable and that thread will immediately be put back in the pool.
                 It provides no benefit and is just a resource waste.

                 If you are wrapping something that is blocking, then use HystrixCommand. If
                 what you're wrapping is async (meaning you don't need a thread to make it
                 async), then HystrixObservableCommand is the right solution, and you don't
                 need a thread.
                 */
                .withExecutionIsolationStrategy(SEMAPHORE)
                .withExecutionIsolationSemaphoreMaxConcurrentRequests(1)
                .withFallbackEnabled(false)
                // Timeouts are set on individual Couchbase operations so that Couchbase client
                // has a chance to clean up
                .withExecutionTimeoutEnabled(false);
    }


    @Override
    protected Observable<AsyncBucket> construct() {
        CouchbaseProperties.BucketProperties bucket = couchbaseProperties.getBucket();

        return couchbaseAsyncClusterFactory.getAsyncClusterInstance()
                .flatMapObservable(cluster -> cluster.clusterManager(
                        couchbaseProperties.getAdminUsername(),
                        couchbaseProperties.getAdminPassword())
                        .map(clusterManager -> Tuple.create(cluster, clusterManager))
                )
                .flatMap(tuple -> {
                    AsyncCluster cluster = tuple.value1();
                    AsyncClusterManager clusterManager = tuple.value2();

                    Observable<Boolean> hasBucket = clusterManager.hasBucket(bucket.getName())
                            .doOnError(t -> log.error("Failed to find if bucket: {} exists.", bucket.getName(), t))
                            .timeout(couchbaseProperties.getClusterOperationTimeoutMillis(), MILLISECONDS)
                            .replay(1)
                            .autoConnect();

                    return hasBucket
                            .map(present -> {
                                log.info("Bucket: {} is: {}.", bucket.getName(),
                                        present ? "already present" : "absent");
                                return !present && bucket.isCreateIfMissing();
                            })
                            .filter(Boolean::booleanValue)
                            .flatMap(x -> {
                                log.info("Creating bucket: {}.", bucket.getName());
                                return clusterManager.insertBucket(bucketSettings(bucket))
                                        .doOnCompleted(() -> log.info("Successfully created bucket: {}.", bucket.getName()))
                                        .doOnError(t -> log.error("Failed to create bucket: {}.", bucket.getName(), t))
                                        .timeout(bucket.getBucketCreateTimeoutMillis(), MILLISECONDS);
                            })
                            .flatMap(x -> openBucket(cluster))
                            .map(b -> {
                                createPrimaryIndex(b)
                                        .toCompletable()
                                        .await(couchbaseProperties.getBlockingOperationTimeoutMillis(), MILLISECONDS);

                                return b;
                            })
                            .switchIfEmpty(hasBucket
                                    .filter(Boolean::booleanValue)
                                    .flatMap(present -> openBucket(cluster)));
                });
    }

    private final BucketSettings bucketSettings(CouchbaseProperties.BucketProperties bucket) {
        return DefaultBucketSettings.builder()
                .name(bucket.getName())
                .enableFlush(bucket.isEnableFlush())
                .quota(bucket.getDefaultQuotaMB())
                .indexReplicas(bucket.isIndexReplicas())
                .build();
    }

    Observable<AsyncN1qlQueryRow> createPrimaryIndex(AsyncBucket bucket) {
        CouchbaseProperties.BucketProperties bucketProperties = couchbaseProperties.getBucket();

        Statement statement = Index.createPrimaryIndex()
                .on(bucketProperties.getName());
        SimpleN1qlQuery query = N1qlQuery.simple(statement);

        return Observable.just(0L)
                // separate index creation so as not to timeout on bucket opening
                .observeOn(scheduler)
                .doOnNext(i -> log.info("Creating primary index."))
                .flatMap(i ->
                        Observable.timer(bucketProperties.getBucketOpenTimeoutMillis(), MILLISECONDS, scheduler)
                                .zipWith(queryExecutor.apply(bucket, query), (j, row) -> row))
                .doOnCompleted(() -> log.info("Successfully created primary index."))
                .doOnError(t -> log.error("Failed to create primary index.", t));
    }

    private final Observable<AsyncBucket> openBucket(AsyncCluster cluster) {
        CouchbaseProperties.BucketProperties bucket = couchbaseProperties.getBucket();
        log.info("Opening bucket: {}.", bucket.getName());

        return cluster.openBucket(bucket.getName())
                .doOnCompleted(() -> log.info("Successfully opened bucket: {}.", bucket.getName()))
                .doOnError(t -> log.error("Failed to open bucket: {}.", bucket.getName(), t))
                .timeout(bucket.getBucketOpenTimeoutMillis(), MILLISECONDS);
    }
}
