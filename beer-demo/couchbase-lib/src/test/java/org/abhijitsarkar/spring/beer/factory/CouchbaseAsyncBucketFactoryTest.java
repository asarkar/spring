package org.abhijitsarkar.spring.beer.factory;

import com.couchbase.client.java.AsyncBucket;
import org.abhijitsarkar.spring.beer.CouchbaseProperties;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.Single;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Abhijit Sarkar
 */
public class CouchbaseAsyncBucketFactoryTest {
    private CouchbaseProperties couchbaseProperties;
    private CouchbaseProperties.BucketProperties bucketProperties;

    @Before
    @SuppressWarnings("unchecked")
    public void beforeEach() {
        couchbaseProperties = new CouchbaseProperties();
        bucketProperties = new CouchbaseProperties.BucketProperties();

        bucketProperties.setName("test");
        bucketProperties.setCreateIfMissing(true);
        couchbaseProperties.setAdminUsername("test");
        couchbaseProperties.setBucket(bucketProperties);
    }

    @Test
    public void testClose() {
        CouchbaseAsyncBucketFactory factory = CouchbaseAsyncBucketFactory.newInstance(
                mock(CouchbaseAsyncClusterFactory.class), couchbaseProperties);

        assertThat(factory).isInstanceOf(CouchbaseAsyncBucketFactory.DefaultCouchbaseAsyncBucketFactory.class);

        CouchbaseAsyncBucketFactory.DefaultCouchbaseAsyncBucketFactory bucketFactory = (CouchbaseAsyncBucketFactory.DefaultCouchbaseAsyncBucketFactory) factory;

        AsyncBucket mock = mock(AsyncBucket.class);
        when(mock.close()).thenReturn(Observable.just(true));
        Single<AsyncBucket> bucket = Single.just(mock);
        bucketFactory.asyncBucket.set(bucket);

        bucketFactory.close(bucket);

        verify(mock).close();
        assertThat(bucketFactory.close(bucket)).isTrue();
    }
}