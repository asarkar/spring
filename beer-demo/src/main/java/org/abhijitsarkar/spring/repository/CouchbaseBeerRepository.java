package org.abhijitsarkar.spring.repository;

import org.abhijitsarkar.spring.domain.Beer;
import org.abhijitsarkar.spring.factory.CouchbaseAsyncBucketFactory;
import org.springframework.stereotype.Repository;

/**
 * @author Abhijit Sarkar
 */
@Repository
public class CouchbaseBeerRepository extends AbstractCouchbaseRepository<Beer> {
    public CouchbaseBeerRepository(CouchbaseAsyncBucketFactory couchbaseAsyncBucketFactory) {
        super(couchbaseAsyncBucketFactory);
    }
}
