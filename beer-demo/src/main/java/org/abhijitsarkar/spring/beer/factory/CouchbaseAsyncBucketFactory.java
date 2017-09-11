package org.abhijitsarkar.spring.beer.factory;

import com.couchbase.client.java.AsyncBucket;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.spring.beer.CouchbaseProperties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import rx.Single;

import java.util.concurrent.atomic.AtomicReference;

import static com.netflix.hystrix.exception.HystrixRuntimeException.FailureType.SHORTCIRCUIT;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Abhijit Sarkar
 */
public interface CouchbaseAsyncBucketFactory {
    static CouchbaseAsyncBucketFactory newInstance(
            CouchbaseAsyncClusterFactory couchbaseAsyncClusterFactory,
            CouchbaseProperties couchbaseProperties) {
        return new DefaultCouchbaseAsyncBucketFactory(couchbaseAsyncClusterFactory, couchbaseProperties);
    }

    Single<AsyncBucket> getAsyncBucketInstance();

    @RequiredArgsConstructor
    @Slf4j
    class DefaultCouchbaseAsyncBucketFactory implements CouchbaseAsyncBucketFactory, InitializingBean, DisposableBean {
        @NonNull
        final CouchbaseAsyncClusterFactory couchbaseAsyncClusterFactory;
        @NonNull
        final CouchbaseProperties couchbaseProperties;

        AtomicReference<Single<AsyncBucket>> asyncBucket = new AtomicReference<>();

        public Single<AsyncBucket> getAsyncBucketInstance() {
            if (asyncBucket.get() == null) {
                HystrixRuntimeException ex = new HystrixRuntimeException(
                        SHORTCIRCUIT,
                        AsyncBucketHystrixObservableCommand.class,
                        String.format("Bucket: %s not open.", couchbaseProperties.getBucket().getName()),
                        new RuntimeException("Hystrix circuit short-circuited and is OPEN"),
                        null);

                return Single.<AsyncBucket>error(ex)
                        .doOnError(t -> afterPropertiesSet());
            }

            return asyncBucket.get();
        }

        boolean close() {
            if (asyncBucket.get() == null) {
                return false;
            }

            CouchbaseProperties.BucketProperties bucket = couchbaseProperties.getBucket();
            requireNonNull(bucket, "BucketProperties must not be null.");

            log.info("Closing bucket: {}.", bucket.getName());

            return asyncBucket
                    .get()
                    .flatMapObservable(AsyncBucket::close)
                    .toCompletable()
                    .doOnError(t -> log.error("Failed to close bucket: {}.", bucket.getName(), t))
                    .await(couchbaseProperties.getBucket().getBucketCloseTimeoutMillis(), MILLISECONDS);
        }

        @Override
        public void destroy() throws Exception {
            close();
        }

        @Override
        public void afterPropertiesSet() {
            String bucketName = couchbaseProperties.getBucket().getName();

            new AsyncBucketHystrixObservableCommand(couchbaseAsyncClusterFactory, couchbaseProperties)
                    .observe()
                    .subscribe(bucket -> {
                                boolean initialized = asyncBucket.compareAndSet(null, Single.just(bucket).cache());
                                log.info("Bucket: {} {} opened.", couchbaseProperties.getBucket().getName(),
                                        initialized ? "successfully" : "cannot be");
                            },
                            error -> log.error("Failed to open bucket: {}.", bucketName, error));
        }
    }
}
