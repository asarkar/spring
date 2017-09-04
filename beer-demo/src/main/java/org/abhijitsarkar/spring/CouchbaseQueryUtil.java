package org.abhijitsarkar.spring;

import com.couchbase.client.core.lang.Tuple;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.AsyncN1qlQueryResult;
import com.couchbase.client.java.query.AsyncN1qlQueryRow;
import com.couchbase.client.java.query.N1qlQuery;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;

import static java.util.stream.Collectors.joining;

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

                    return result.finalSuccess()
                            .doOnNext(success -> {
                                if (!success) {
                                    result.errors()
                                            .map(JsonObject::toString)
                                            .toList()
                                            .doOnNext(messages -> {
                                                String errorMessage = messages.stream()
                                                        .collect(joining(","));

                                                log.error("Failed to execute query: {}. Errors: {}.",
                                                        tuple.value1().statement(),
                                                        errorMessage
                                                );
                                            });
                                }
                            })
                            .filter(Boolean::booleanValue)
                            .flatMap(x -> result.rows());
                });
    }
}
