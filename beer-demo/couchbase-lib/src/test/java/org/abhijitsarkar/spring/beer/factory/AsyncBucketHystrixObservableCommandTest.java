package org.abhijitsarkar.spring.beer.factory;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.AsyncCluster;
import com.couchbase.client.java.cluster.AsyncClusterManager;
import com.couchbase.client.java.cluster.BucketSettings;
import com.couchbase.client.java.query.AsyncN1qlQueryRow;
import com.couchbase.client.java.query.N1qlQuery;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixObservableCommand;
import org.abhijitsarkar.spring.beer.CouchbaseProperties;
import org.abhijitsarkar.spring.beer.CouchbaseProperties.BucketProperties;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Abhijit Sarkar
 */
public class AsyncBucketHystrixObservableCommandTest {
    private AsyncCluster mockCluster;
    private CouchbaseProperties couchbaseProperties;
    private AsyncClusterManager mockClusterManager;
    private CouchbaseAsyncClusterFactory mockClusterFactory;
    private AsyncBucket mockBucket;
    private BiFunction<AsyncBucket, N1qlQuery, Observable<AsyncN1qlQueryRow>> mockQueryExecutor;
    private BucketProperties bucket;
    private TestScheduler testScheduler;

    @Before
    @SuppressWarnings("unchecked")
    public void beforeEach() {
        testScheduler = Schedulers.test();
        couchbaseProperties = new CouchbaseProperties();
        bucket = new BucketProperties();
        mockCluster = mock(AsyncCluster.class);
        mockClusterManager = mock(AsyncClusterManager.class);
        mockClusterFactory = mock(CouchbaseAsyncClusterFactory.class);
        mockQueryExecutor = mock(BiFunction.class);
        mockBucket = mock(AsyncBucket.class);

        when(mockClusterFactory.getAsyncClusterInstance()).thenReturn(Single.just(mockCluster));
        when(mockCluster.clusterManager(anyString(), anyString())).thenReturn(Observable.just(mockClusterManager));
        when(mockCluster.openBucket(anyString())).thenReturn(Observable.just(mockBucket));
        when(mockQueryExecutor.apply(eq(mockBucket), any(N1qlQuery.class)))
                .thenReturn(Observable.just(mock(AsyncN1qlQueryRow.class)));

        bucket.setName("test");
        bucket.setCreateIfMissing(true);
        bucket.setBucketOpenTimeoutMillis(1L);
        bucket.setBucketCreateTimeoutMillis(1L);
        couchbaseProperties.setClusterOperationTimeoutMillis(1L);
        couchbaseProperties.setBlockingOperationTimeoutMillis(1L);
        couchbaseProperties.setAdminUsername("test");
        couchbaseProperties.setBucket(bucket);
    }

    @Test
    public void testCBPropertiesMustNotBeNull() {
        assertThatNullPointerException().isThrownBy(() -> new AsyncBucketHystrixObservableCommand(
                mock(CouchbaseAsyncClusterFactory.class), null));
    }

    @Test
    public void testAsyncClusterFactoryMustNotBeNull() {
        assertThatNullPointerException().isThrownBy(() -> new AsyncBucketHystrixObservableCommand(
                null, new CouchbaseProperties()));
    }

    @Test
    public void testCBAdminUsernameMustNotBeNull() {
        couchbaseProperties.setAdminUsername(null);

        assertThatNullPointerException().isThrownBy(() ->
                new AsyncBucketHystrixObservableCommand(mockClusterFactory, couchbaseProperties));
    }

    @Test
    public void testBucketPropertiesMustNotBeNull() {
        couchbaseProperties.setBucket(null);

        assertThatNullPointerException().isThrownBy(() ->
                new AsyncBucketHystrixObservableCommand(mockClusterFactory, couchbaseProperties));
    }

    @Test
    public void testBucketNameMustNotBeNull() {
        couchbaseProperties.getBucket().setName(null);

        assertThatNullPointerException().isThrownBy(() ->
                new AsyncBucketHystrixObservableCommand(mockClusterFactory, couchbaseProperties));
    }

    @Test
    public void testCreateBucketIfAbsent() {
        when(mockClusterManager.hasBucket(anyString())).thenReturn(Observable.just(false));
        when(mockClusterManager.insertBucket(any(BucketSettings.class)))
                .thenReturn(Observable.just(mock(BucketSettings.class)));

        AsyncBucketHystrixObservableCommand cmd =
                new AsyncBucketHystrixObservableCommand(mockClusterFactory, couchbaseProperties);

        cmd.queryExecutor = mockQueryExecutor;
        cmd.scheduler = testScheduler;

        TestSubscriber<AsyncBucket> subscriber = new TestSubscriber<>();

        cmd.observe()
                .subscribe(subscriber);

        testScheduler.advanceTimeBy(bucket.getBucketOpenTimeoutMillis(), TimeUnit.MILLISECONDS);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        verify(mockClusterManager).insertBucket(argThat(settings ->
                settings.name().equals(bucket.getName())
                        && settings.enableFlush() == bucket.isEnableFlush()
                        && settings.quota() == bucket.getDefaultQuotaMB()
                        && settings.indexReplicas() == bucket.isIndexReplicas()
                        && bucket.isCreateIfMissing()
        ));
        verify(mockCluster).openBucket(bucket.getName());
    }

    @Test
    public void testCreateBucketError() {
        when(mockClusterManager.hasBucket(anyString())).thenReturn(Observable.just(false));

        AsyncBucketHystrixObservableCommand cmd =
                new AsyncBucketHystrixObservableCommand(mockClusterFactory, couchbaseProperties);

        cmd.queryExecutor = mockQueryExecutor;

        when(mockClusterManager.insertBucket(any(BucketSettings.class)))
                .thenThrow(new RuntimeException("test"));

        TestSubscriber<AsyncBucket> subscriber = new TestSubscriber<>();

        cmd.observe()
                .subscribe(subscriber);

        subscriber.assertError(RuntimeException.class);
        subscriber.assertNotCompleted();

        verify(mockCluster, never()).openBucket(bucket.getName());
    }

    @Test
    public void testOpenBucketError() {
        when(mockClusterManager.hasBucket(anyString())).thenReturn(Observable.just(true));

        AsyncBucketHystrixObservableCommand cmd =
                new AsyncBucketHystrixObservableCommand(mockClusterFactory, couchbaseProperties);

        cmd.queryExecutor = mockQueryExecutor;

        when(mockCluster.openBucket(anyString()))
                .thenThrow(new RuntimeException("test"));
        TestSubscriber<AsyncBucket> subscriber = new TestSubscriber<>();

        cmd.observe()
                .subscribe(subscriber);

        subscriber.assertError(RuntimeException.class);
        subscriber.assertNotCompleted();
    }

    @Test
    public void testCreateClusterError() {
        when(mockClusterFactory.getAsyncClusterInstance())
                .thenThrow(new RuntimeException("test"));

        AsyncBucketHystrixObservableCommand cmd =
                new AsyncBucketHystrixObservableCommand(mockClusterFactory, couchbaseProperties);

        cmd.queryExecutor = mockQueryExecutor;

        TestSubscriber<AsyncBucket> subscriber = new TestSubscriber<>();

        cmd.observe()
                .subscribe(subscriber);

        subscriber.assertError(RuntimeException.class);
        subscriber.assertNotCompleted();

        verify(mockClusterManager, never()).insertBucket(any(BucketSettings.class));
        verify(mockCluster, never()).openBucket(bucket.getName());
    }

    @Test
    public void testDoesnotCreateBucketIfPresent() {
        when(mockClusterManager.hasBucket(anyString())).thenReturn(Observable.just(true));

        HystrixObservableCommand.Setter setter = HystrixObservableCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("test-grp"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("testDoesnotCreateBucketIfPresent-cmd"))
                .andCommandPropertiesDefaults(AsyncBucketHystrixObservableCommand.commandPropertiesSetter(bucket)
                        .withCircuitBreakerEnabled(false));

        AsyncBucketHystrixObservableCommand cmd =
                new AsyncBucketHystrixObservableCommand(mockClusterFactory, couchbaseProperties, setter);

        cmd.queryExecutor = mockQueryExecutor;

        TestSubscriber<AsyncBucket> subscriber = new TestSubscriber<>();

        cmd.observe()
                .subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        verify(mockClusterManager, never()).insertBucket(any(BucketSettings.class));
        verify(mockQueryExecutor, never()).apply(eq(mockBucket), any(N1qlQuery.class));
        verify(mockCluster).openBucket(bucket.getName());
    }

    @Test
    public void testDoesnotOpenBucketIfNotPresent() {
        when(mockClusterManager.hasBucket(anyString())).thenReturn(Observable.just(false));
        bucket.setCreateIfMissing(false);

        HystrixObservableCommand.Setter setter = HystrixObservableCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("test-grp"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("testDoesnotOpenBucketIfNotPresent-cmd"))
                .andCommandPropertiesDefaults(AsyncBucketHystrixObservableCommand.commandPropertiesSetter(bucket)
                        .withCircuitBreakerEnabled(false));

        AsyncBucketHystrixObservableCommand cmd =
                new AsyncBucketHystrixObservableCommand(mockClusterFactory, couchbaseProperties, setter);

        cmd.queryExecutor = mockQueryExecutor;

        TestSubscriber<AsyncBucket> subscriber = new TestSubscriber<>();

        cmd.observe()
                .subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertNoValues();

        verify(mockClusterManager, never()).insertBucket(any(BucketSettings.class));
        verify(mockCluster, never()).openBucket(bucket.getName());
        verify(mockQueryExecutor, never()).apply(eq(mockBucket), any(N1qlQuery.class));
    }

    @Test
    public void testCreatePrimaryIndex() {
        AsyncBucketHystrixObservableCommand cmd =
                new AsyncBucketHystrixObservableCommand(mockClusterFactory, couchbaseProperties);

        cmd.queryExecutor = mockQueryExecutor;
        cmd.scheduler = testScheduler;

        TestSubscriber<AsyncN1qlQueryRow> subscriber = new TestSubscriber<>();

        cmd.createPrimaryIndex(mockBucket)
                .subscribe(subscriber);

        testScheduler.advanceTimeBy(bucket.getBucketOpenTimeoutMillis(), TimeUnit.MILLISECONDS);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        String expectedQueryStatement = "CREATE PRIMARY INDEX ON `test`";
        verify(mockQueryExecutor).apply(eq(mockBucket),
                argThat(query -> query.statement().toString().equals(expectedQueryStatement)));
    }

    @Test
    public void testCreatePrimaryIndexError() {
        AsyncBucketHystrixObservableCommand cmd =
                new AsyncBucketHystrixObservableCommand(mockClusterFactory, couchbaseProperties);

        cmd.queryExecutor = mockQueryExecutor;
        cmd.scheduler = testScheduler;

        TestSubscriber<AsyncN1qlQueryRow> subscriber = new TestSubscriber<>();
        when(mockClusterManager.hasBucket(anyString())).thenReturn(Observable.just(true));

        when(mockQueryExecutor.apply(eq(mockBucket), any(N1qlQuery.class)))
                .thenThrow(new RuntimeException("test"));

        cmd.createPrimaryIndex(mockBucket)
                .subscribe(subscriber);

        testScheduler.advanceTimeBy(bucket.getBucketOpenTimeoutMillis(), TimeUnit.MILLISECONDS);

        subscriber.assertError(RuntimeException.class);
        subscriber.assertNotCompleted();
    }

    @Test
    public void testCreateBucketTimeout() {
        long delay = bucket.getBucketOpenTimeoutMillis() * 3;

        when(mockClusterManager.hasBucket(anyString())).thenReturn(Observable.just(false));
        when(mockClusterManager.insertBucket(any(BucketSettings.class)))
                .thenReturn(Observable.just(mock(BucketSettings.class))
                        .delay(delay, TimeUnit.MILLISECONDS));

        AsyncBucketHystrixObservableCommand cmd =
                new AsyncBucketHystrixObservableCommand(mockClusterFactory, couchbaseProperties);

        cmd.queryExecutor = mockQueryExecutor;
        cmd.scheduler = testScheduler;

        TestSubscriber<AsyncBucket> subscriber = new TestSubscriber<>();

        cmd.observe()
                .subscribe(subscriber);

        testScheduler.advanceTimeBy(delay, TimeUnit.MILLISECONDS);

        subscriber.assertNoValues();
        subscriber.assertNotCompleted();
    }

    @Test
    public void testOpenBucketTimeout() {
        long delay = bucket.getBucketOpenTimeoutMillis() * 3;

        when(mockClusterManager.hasBucket(anyString())).thenReturn(Observable.just(true));
        when(mockCluster.openBucket(anyString())).thenReturn(Observable.just(mockBucket)
                .delay(delay, TimeUnit.MILLISECONDS));

        AsyncBucketHystrixObservableCommand cmd =
                new AsyncBucketHystrixObservableCommand(mockClusterFactory, couchbaseProperties);

        cmd.queryExecutor = mockQueryExecutor;
        cmd.scheduler = testScheduler;

        TestSubscriber<AsyncBucket> subscriber = new TestSubscriber<>();

        cmd.observe()
                .subscribe(subscriber);

        testScheduler.advanceTimeBy(delay, TimeUnit.MILLISECONDS);

        subscriber.assertNoValues();
        subscriber.assertNotCompleted();
    }

    @Test
    public void testCreateClusterTimeout() {
        long delay = couchbaseProperties.getClusterOperationTimeoutMillis() * 3;

        when(mockClusterFactory.getAsyncClusterInstance()).thenReturn(Single.just(mockCluster)
                .delay(delay, TimeUnit.MILLISECONDS));

        AsyncBucketHystrixObservableCommand cmd =
                new AsyncBucketHystrixObservableCommand(mockClusterFactory, couchbaseProperties);

        cmd.queryExecutor = mockQueryExecutor;
        cmd.scheduler = testScheduler;

        TestSubscriber<AsyncBucket> subscriber = new TestSubscriber<>();

        cmd.observe()
                .subscribe(subscriber);

        testScheduler.advanceTimeBy(delay, TimeUnit.MILLISECONDS);

        subscriber.assertNoValues();
        subscriber.assertNotCompleted();
    }
}