package org.abhijitsarkar.spring.beer.repository;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.query.AsyncN1qlQueryRow;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.SimpleN1qlQuery;
import com.couchbase.client.java.query.dsl.path.AsPath;
import com.couchbase.client.java.query.dsl.path.GroupByPath;
import com.couchbase.client.java.query.dsl.path.LetPath;
import org.abhijitsarkar.spring.beer.CouchbaseQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Single;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static java.util.Collections.emptyList;

/**
 * @author Abhijit Sarkar
 */
public interface CouchbaseRepository<T> {
    Logger LOGGER = LoggerFactory.getLogger(CouchbaseRepository.class);

    // indirection for testing
    default BiFunction<AsyncBucket, N1qlQuery, Observable<AsyncN1qlQueryRow>> queryExecutor() {
        return CouchbaseQueryUtil::executeN1qlQuery;
    }

    default Single<T> findOne(String id) {
        return asyncBucket()
                .flatMap(bucket -> bucket.get(id, RawJsonDocument.class).toSingle())
                .map(RawJsonDocument::content)
                .flatMap(this::convert)
                .doOnError(t -> {
                    if (t instanceof NoSuchElementException) {
                        LOGGER.debug("No doc found with id: {}", id);
                    } else {
                        LOGGER.error("Failed to find doc with id: {}.", id, t);
                    }
                });
    }

    default Observable<T> findAll(String fieldName, String fieldValue) {
        return findAllByIds(emptyList(), fieldName, fieldValue);
    }

    default Observable<T> findAllByIds(final List<String> ids, String fieldName, String fieldValue) {
        return asyncBucket()
                .flatMapObservable(bucket -> {
                    AsPath temp1 = select(path(bucket.name(), "*"))//select(arrayAgg(i(bucket.name())))
                            .from(i(bucket.name()));

                    LetPath temp2 = temp1;
                    if (!ids.isEmpty()) {
                        temp2 = temp1
                                .useKeys(JsonArray.from(ids));
                    }

                    GroupByPath statement = temp2.where(path(fieldName).eq(s(fieldValue)));

                    SimpleN1qlQuery query = N1qlQuery.simple(statement);

                    return queryExecutor().apply(bucket, query);
                })
                .doOnError(t -> LOGGER.error("Failed to query docs with ids: {}, typeField: {} and type: {}.",
                        ids, fieldName, fieldValue, t))
                .flatMap(row -> convert(row.value().toString()).toObservable());
    }

    Single<AsyncBucket> asyncBucket();

    Single<T> convert(String json);
}