package org.abhijitsarkar.spring.beer.repository;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.AsyncN1qlQueryRow;
import com.couchbase.client.java.query.N1qlQuery;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import java.util.Arrays;
import java.util.function.BiFunction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Abhijit Sarkar
 */
public class CouchbaseRepositoryTest {
    private AsyncBucket mockBucket;
    private BiFunction<AsyncBucket, N1qlQuery, Observable<AsyncN1qlQueryRow>> mockQueryExecutor;

    private CouchbaseRepository<String> testCouchbaseRepository = new CouchbaseRepository<String>() {
        @Override
        public Single<AsyncBucket> asyncBucket() {
            return Single.just(mockBucket);
        }

        @Override
        public Single<String> convert(String json) {
            return Single.just(json);
        }

        @Override
        public BiFunction<AsyncBucket, N1qlQuery, Observable<AsyncN1qlQueryRow>> queryExecutor() {
            return mockQueryExecutor;
        }
    };

    @Before
    @SuppressWarnings("unchecked")
    public void beforeEach() {
        mockBucket = mock(AsyncBucket.class);
        mockQueryExecutor = mock(BiFunction.class);

        when(mockBucket.name()).thenReturn("test");
        when(mockBucket.get(anyString(), eq(RawJsonDocument.class)))
                .thenReturn(Observable.just(RawJsonDocument.create("id", "content")));
        AsyncN1qlQueryRow mockRow = mock(AsyncN1qlQueryRow.class);
        when(mockRow.value()).thenReturn(JsonObject.create().put("key", "value"));
        when(mockQueryExecutor.apply(eq(mockBucket), any(N1qlQuery.class)))
                .thenReturn(Observable.just(mockRow));
    }

    @Test
    public void testFindOne() {
        TestSubscriber<String> subscriber = new TestSubscriber<>();

        testCouchbaseRepository.findOne("id")
                .subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertValues("content");
    }

    @Test
    public void testFindOneError() {
        when(mockBucket.get(anyString(), eq(RawJsonDocument.class)))
                .thenReturn(Observable.error(new RuntimeException("test")));

        TestSubscriber<String> subscriber = new TestSubscriber<>();

        testCouchbaseRepository.findOne("id")
                .subscribe(subscriber);

        subscriber.assertError(RuntimeException.class);
    }

    @Test
    public void testFindByIds() {
        TestSubscriber<String> subscriber = new TestSubscriber<>();

        testCouchbaseRepository.findByIds(Arrays.asList("id"))
                .subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertValues("{\"key\":\"value\"}");

        String expectedQueryStatement = "SELECT `test`.* FROM `test` USE KEYS [\"id\"]";
        verify(mockQueryExecutor).apply(eq(mockBucket),
                argThat(query -> {
                    System.out.println(query.statement().toString());
                    return query.statement().toString().equals(expectedQueryStatement);
                }));
    }
}