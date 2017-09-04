package org.abhijitsarkar.spring.repository;

import com.couchbase.client.java.AsyncBucket;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.abhijitsarkar.spring.factory.CouchbaseAsyncBucketFactory;
import rx.Single;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

/**
 * @author Abhijit Sarkar
 */
@RequiredArgsConstructor
public class AbstractCouchbaseRepository<T> implements CouchbaseRepository<T> {
    private final CouchbaseAsyncBucketFactory couchbaseAsyncBucketFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Single<AsyncBucket> asyncBucket() {
        return couchbaseAsyncBucketFactory.getInstance();
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
