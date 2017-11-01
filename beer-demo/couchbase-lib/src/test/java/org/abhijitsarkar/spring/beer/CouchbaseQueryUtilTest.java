package org.abhijitsarkar.spring.beer;

import com.couchbase.client.core.CouchbaseException;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.AsyncN1qlQueryResult;
import com.couchbase.client.java.query.AsyncN1qlQueryRow;
import com.couchbase.client.java.query.N1qlMetrics;
import com.couchbase.client.java.query.N1qlQuery;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.abhijitsarkar.spring.beer.CouchbaseQueryUtil.executeN1qlQuery;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Abhijit Sarkar
 */
public class CouchbaseQueryUtilTest {
    AsyncBucket mockBucket;
    N1qlQuery mockQuery;
    AsyncN1qlQueryResult mockResult;

    @Before
    public void beforeEach() {
        mockBucket = mock(AsyncBucket.class);
        mockQuery = mock(N1qlQuery.class);
        mockResult = mock(AsyncN1qlQueryResult.class);

        when(mockBucket.query(mockQuery)).thenReturn(Observable.just(mockResult));
        when(mockResult.info()).thenReturn(Observable.just(new N1qlMetrics(JsonObject.empty())));
    }

    @Test
    public void testExecuteN1qlQuery() {
        AsyncN1qlQueryRow mockRow = mock(AsyncN1qlQueryRow.class);

        when(mockResult.rows()).thenReturn(Observable.just(mockRow));
        when(mockResult.finalSuccess()).thenReturn(Observable.just(true));

        TestSubscriber<AsyncN1qlQueryRow> subscriber = new TestSubscriber<>();

        executeN1qlQuery(mockBucket, mockQuery)
                .subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        assertThat(subscriber.getOnNextEvents()).containsExactlyInAnyOrder(mockRow);

        verify(mockResult, never()).errors();
    }

    @Test
    public void testExecuteN1qlQueryError() {
        when(mockResult.finalSuccess()).thenReturn(Observable.just(false));
        JsonObject jsonObject = JsonObject.fromJson("{\"test\" : \"error\"}");
        when(mockResult.errors()).thenReturn(Observable.just(jsonObject));

        TestSubscriber<AsyncN1qlQueryRow> subscriber = new TestSubscriber<>();

        executeN1qlQuery(mockBucket, mockQuery)
                .subscribe(subscriber);

        subscriber.assertError(CouchbaseException.class);

        verify(mockResult, never()).rows();
    }
}