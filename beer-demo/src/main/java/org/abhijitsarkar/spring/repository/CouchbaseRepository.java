package org.abhijitsarkar.spring.repository;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.SimpleN1qlQuery;
import com.couchbase.client.java.query.dsl.path.AsPath;
import com.couchbase.client.java.query.dsl.path.GroupByPath;
import com.couchbase.client.java.query.dsl.path.LetPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Single;

import java.util.List;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static java.util.Collections.emptyList;
import static org.abhijitsarkar.spring.CouchbaseQueryUtil.executeN1qlQuery;

/**
 * @author Abhijit Sarkar
 */
public interface CouchbaseRepository<T> {
    Logger log = LoggerFactory.getLogger(CouchbaseRepository.class);

    default Single<T> findOne(String id) {
        return asyncBucket()
                .flatMap(bucket -> bucket.get(id, RawJsonDocument.class).toSingle())
                .map(RawJsonDocument::content)
                .flatMap(this::convert)
                .doOnError(t -> log.error("Failed to look up doc with id: {}.", id, t));
    }

    default Observable<T> findAll(String typeField, String type) {
        return findAll(emptyList(), typeField, type);
    }

    default Observable<T> findAll(final List<String> ids, String typeField, String type) {
        return asyncBucket()
                .flatMapObservable(bucket -> {
                    AsPath temp1 = select(path(bucket.name(), "*"))//select(arrayAgg(i(bucket.name())))
                            .from(i(bucket.name()));

                    LetPath temp2 = temp1;
                    if (!ids.isEmpty()) {
                        temp2 = temp1
                                .useKeys(JsonArray.from(ids));
                    }

                    GroupByPath statement = temp2.where(path(typeField).eq(s(type)));

                    SimpleN1qlQuery query = N1qlQuery.simple(statement);

                    return executeN1qlQuery(bucket, query);
                })
                .doOnError(t -> log.error("Failed to query docs with ids: {}, typeField: {} and type: {}.",
                        ids, typeField, type, t))
                .flatMap(row -> convert(row.value().toString()).toObservable());
    }

    Single<AsyncBucket> asyncBucket();

    Single<T> convert(String json);
}