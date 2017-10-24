package org.abhijitsarkar.spring.beer.factory;

import com.couchbase.client.java.AsyncCluster;
import com.couchbase.client.java.CouchbaseAsyncCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import org.abhijitsarkar.spring.beer.CouchbaseProperties;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Abhijit Sarkar
 */
@SuppressWarnings("unchecked")
public class CouchbaseAsyncClusterFactoryTest {
    private BiFunction<CouchbaseEnvironment, List<String>, CouchbaseAsyncCluster> mockClusterCreator;
    private CouchbaseAsyncCluster mockCluster;

    @Before
    public void beforeEach() {
        mockClusterCreator = mock(BiFunction.class);
        mockCluster = mock(CouchbaseAsyncCluster.class);

        when(mockClusterCreator.apply(any(CouchbaseEnvironment.class), any(List.class)))
                .thenReturn(mockCluster);
    }

    @Test
    public void testCreateCluster() {
        CouchbaseAsyncClusterFactory factory =
                CouchbaseAsyncClusterFactory.newInstance(null, new CouchbaseProperties());

        assertThat(factory).isInstanceOf(
                CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory.class);

        CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory clusterFactory = (CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory) factory;

        clusterFactory.clusterCreator = mockClusterCreator;

        TestSubscriber<AsyncCluster> subscriber = new TestSubscriber<>();
        clusterFactory.newAsyncClusterInstance()
                .subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertValue(mockCluster);
    }

    @Test
    public void testCreateClusterError() {
        when(mockClusterCreator.apply(any(CouchbaseEnvironment.class), any(List.class)))
                .thenThrow(new RuntimeException("test"));
        CouchbaseAsyncClusterFactory factory =
                CouchbaseAsyncClusterFactory.newInstance(null, new CouchbaseProperties());

        assertThat(factory).isInstanceOf(
                CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory.class);

        CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory clusterFactory = (CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory) factory;

        clusterFactory.clusterCreator = mockClusterCreator;

        TestSubscriber<AsyncCluster> subscriber = new TestSubscriber<>();
        clusterFactory.newAsyncClusterInstance()
                .subscribe(subscriber);

        subscriber.assertError(RuntimeException.class);
    }

    @Test
    public void testCreateCBEnvIfNull() {
        CouchbaseAsyncClusterFactory factory =
                CouchbaseAsyncClusterFactory.newInstance(null, new CouchbaseProperties());

        assertThat(factory).isInstanceOf(
                CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory.class);

        CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory clusterFactory = (CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory) factory;

        assertThat(clusterFactory.environment).isInstanceOf(DefaultCouchbaseEnvironment.class);
    }

    @Test
    public void testDoesnotCreateCBEnvIfNotNull() {
        DefaultCouchbaseEnvironment couchbaseEnvironment = DefaultCouchbaseEnvironment.create();

        CouchbaseAsyncClusterFactory factory =
                CouchbaseAsyncClusterFactory.newInstance(couchbaseEnvironment, new CouchbaseProperties());

        assertThat(factory).isInstanceOf(
                CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory.class);

        CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory clusterFactory = (CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory) factory;

        assertThat(clusterFactory.environment).isSameAs(couchbaseEnvironment);
    }

    @Test
    public void testCBPropertiesMustNotBeNull() {
        assertThatNullPointerException().isThrownBy(() ->
                CouchbaseAsyncClusterFactory.newInstance(null, null));
    }

    @Test
    public void testDisconnect() {
        CouchbaseAsyncClusterFactory factory =
                CouchbaseAsyncClusterFactory.newInstance(null, new CouchbaseProperties());

        assertThat(factory).isInstanceOf(
                CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory.class);

        CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory clusterFactory = (CouchbaseAsyncClusterFactory.DefaultCouchbaseAsyncClusterFactory) factory;

        AsyncCluster mock = mock(AsyncCluster.class);
        when(mock.disconnect()).thenReturn(Observable.just(true));
        clusterFactory.asyncCluster = Single.just(mock);

        clusterFactory.disconnect();

        verify(mock).disconnect();
        assertThat(clusterFactory.disconnect()).isTrue();
    }
}