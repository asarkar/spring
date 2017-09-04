package org.abhijitsarkar.spring;

import com.couchbase.client.core.CouchbaseException;
import com.couchbase.client.core.lang.Tuple;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.query.AsyncN1qlQueryResult;
import com.couchbase.client.java.query.AsyncN1qlQueryRow;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.Statement;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
public class CouchbaseQueryUtil {
    private CouchbaseQueryUtil() {
    }

    public static final Observable<AsyncN1qlQueryRow> executeN1qlQuery(AsyncBucket bucket, N1qlQuery query) {
        log.debug("Executing query: {}.", query.statement());

        return bucket.query(query)
                .map(result -> Tuple.create(query, result))
                .flatMap(tuple -> {
                    AsyncN1qlQueryResult result = tuple.value2();
                    Statement statement = tuple.value1().statement();

                    return result
                            .finalSuccess()
                            .defaultIfEmpty(false)
                            .flatMap(success -> {
                                if (success) {
                                    return result.rows();
                                } else {
                                    // https://developer.couchbase.com/documentation/server/current/sdk/java/handling-error-conditions.html
                                    return result.errors()
                                            .map(error -> {
                                                String errorMessage = String.format("Failed to execute query: %s, " +
                                                        "error: %s.", statement, error.toString());
                                                return new CouchbaseException(errorMessage);
                                            })
                                            .flatMap(Observable::error);
                                }
                            });
                });
    }
}
