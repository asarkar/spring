package org.abhijitsarkar.spring.beer;

import com.couchbase.client.core.CouchbaseException;
import com.couchbase.client.core.lang.Tuple;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.query.AsyncN1qlQueryResult;
import com.couchbase.client.java.query.AsyncN1qlQueryRow;
import com.couchbase.client.java.query.N1qlMetrics;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.Statement;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;

/**
 * @author Abhijit Sarkar
 * Similar to N1qlQueryExecutor, but not quiet. Doesn't require bucket credentials.
 */
@Slf4j
public final class CouchbaseQueryUtil {
    private CouchbaseQueryUtil() {
    }

    public static Observable<AsyncN1qlQueryRow> executeN1qlQuery(AsyncBucket bucket, N1qlQuery query) {
        log.debug("Executing query: {}.", query.statement());

        return bucket.query(query)
                .map(result -> Tuple.create(query, result))
                .flatMap(tuple1 -> {
                    AsyncN1qlQueryResult result = tuple1.value2();
                    Statement statement = tuple1.value1().statement();

                    return result
                            .finalSuccess()
                            .defaultIfEmpty(false)
                            .zipWith(result.info(), Tuple::create)
                            .flatMap(tuple2 -> {
                                boolean success = tuple2.value1();
                                N1qlMetrics metrics = tuple2.value2();

                                log.debug("Query metrics: {}.", metrics);

                                if (success) {
                                    log.debug("Successfully executed query: {}.", statement);

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
