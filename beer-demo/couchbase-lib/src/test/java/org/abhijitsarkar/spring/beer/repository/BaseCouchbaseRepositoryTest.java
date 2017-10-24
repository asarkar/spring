package org.abhijitsarkar.spring.beer.repository;

import com.couchbase.client.java.AsyncBucket;
import org.abhijitsarkar.spring.beer.factory.CouchbaseAsyncBucketFactory;
import org.junit.Before;
import org.junit.Test;
import rx.Single;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Abhijit Sarkar
 */
public class BaseCouchbaseRepositoryTest {
    private CouchbaseAsyncBucketFactory mockBucketFactory;
    private AsyncBucket mockBucket;

    @Before
    public void beforeEach() {
        mockBucketFactory = mock(CouchbaseAsyncBucketFactory.class);
        mockBucket = mock(AsyncBucket.class);
        when(mockBucketFactory.getAsyncBucketInstance()).thenReturn(Single.just(mockBucket));
    }

    @Test
    public void testAsyncBucket() {
        BaseCouchbaseRepository<TestPojo> baseCouchbaseRepository = new BaseCouchbaseRepository<>();
        baseCouchbaseRepository.couchbaseAsyncBucketFactory = mockBucketFactory;

        TestSubscriber<AsyncBucket> subscriber = new TestSubscriber<>();

        baseCouchbaseRepository.asyncBucket()
                .subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertValue(mockBucket);
    }

    @Test
    public void testAsyncBucketError() {
        when(mockBucketFactory.getAsyncBucketInstance()).thenReturn(Single.error(new RuntimeException("test")));

        BaseCouchbaseRepository<TestPojo> baseCouchbaseRepository = new BaseCouchbaseRepository<>();
        baseCouchbaseRepository.couchbaseAsyncBucketFactory = mockBucketFactory;

        TestSubscriber<AsyncBucket> subscriber = new TestSubscriber<>();

        baseCouchbaseRepository.asyncBucket()
                .subscribe(subscriber);

        subscriber.assertError(RuntimeException.class);
    }

    @Test
    public void testObjectMapperMustNotBeNull() {
        assertThatNullPointerException().isThrownBy(() ->
                new BaseCouchbaseRepository<>(null));
    }

    @Test
    public void testCreateObjectMapperIfNull() {
        BaseCouchbaseRepository<TestPojo> baseCouchbaseRepository = new BaseCouchbaseRepository<>();
        baseCouchbaseRepository.couchbaseAsyncBucketFactory = mockBucketFactory;

        assertThat(baseCouchbaseRepository.objectMapper).isNotNull();
    }

    @Test
    public void testConvert() {
        TestRepository testRepository = new TestRepository(mockBucketFactory);

        TestSubscriber<TestPojo> subscriber = new TestSubscriber<>();
        testRepository.convert("{\"key\":\"value\"}")
                .subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        TestPojo testPojo = subscriber.getOnNextEvents().get(0);

        assertThat(testPojo.key).isEqualTo("value");
    }

    public static class TestRepository extends BaseCouchbaseRepository<TestPojo> {
        public TestRepository(CouchbaseAsyncBucketFactory couchbaseAsyncBucketFactory) {
            super.couchbaseAsyncBucketFactory = couchbaseAsyncBucketFactory;
        }
    }

    public static class TestPojo {
        public String key;
    }
}