package org.abhijitsarkar.spring.beer.repository;

import com.couchbase.client.java.AsyncBucket;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.abhijitsarkar.spring.beer.factory.CouchbaseAsyncBucketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import rx.Single;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import static java.util.Objects.requireNonNull;

/**
 * @author Abhijit Sarkar
 */
public class BaseCouchbaseRepository<T> implements CouchbaseRepository<T> {
    private final ObjectMapper objectMapper;
    @Autowired
    private CouchbaseAsyncBucketFactory couchbaseAsyncBucketFactory;

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

    @Override
    @SuppressWarnings("unchecked")
    public Single<T> convert(String json) {
        Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        try {
            return Single.just(objectMapper.readValue(json, clazz));
        } catch (IOException e) {
            return Single.error(e);
        }
    }
}
