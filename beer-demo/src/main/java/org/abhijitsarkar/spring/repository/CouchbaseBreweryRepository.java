package org.abhijitsarkar.spring.repository;

import org.abhijitsarkar.spring.domain.Brewery;
import org.abhijitsarkar.spring.factory.CouchbaseAsyncBucketFactory;
import org.springframework.stereotype.Repository;

/**
 * @author Abhijit Sarkar
 */
@Repository
public class CouchbaseBreweryRepository extends AbstractCouchbaseRepository<Brewery> {
    public CouchbaseBreweryRepository(CouchbaseAsyncBucketFactory couchbaseAsyncBucketFactory) {
        super(couchbaseAsyncBucketFactory);
    }
}
