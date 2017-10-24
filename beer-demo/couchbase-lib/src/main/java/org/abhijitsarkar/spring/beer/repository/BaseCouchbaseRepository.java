package org.abhijitsarkar.spring.beer.repository;

import com.couchbase.client.java.AsyncBucket;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.spring.beer.CouchbaseProperties;
import org.abhijitsarkar.spring.beer.factory.CouchbaseAsyncBucketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import rx.Single;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import static java.util.Objects.requireNonNull;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
public class BaseCouchbaseRepository<T> implements CouchbaseRepository<T> {
    private static final String MSG_COUCHBASE_BLOCKING_CALL_FAILED = "Couchbase blocking call failed.";
    final ObjectMapper objectMapper;
    @Autowired
    CouchbaseAsyncBucketFactory couchbaseAsyncBucketFactory;
    @Autowired
    CouchbaseProperties couchbaseProperties;

    public BaseCouchbaseRepository() {
        this(new ObjectMapper());
    }

    public BaseCouchbaseRepository(ObjectMapper objectMapper) {
        requireNonNull(objectMapper, "ObjectMapper must not be null.");

        this.objectMapper = objectMapper;
    }

    @Override
    public Single<AsyncBucket> asyncBucket() {
        return couchbaseAsyncBucketFactory.getAsyncBucketInstance();
    }

    @SuppressWarnings("unchecked")
    private final Class<T> getGenericClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public Single<T> convert(String json) {
        try {
            return Single.just(objectMapper.readValue(json, getGenericClass()));
        } catch (IOException e) {
            return Single.error(e);
        }
    }
}
