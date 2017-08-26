package org.abhijitsarkar.spring.repository.couchbase;

import org.abhijitsarkar.spring.domain.Beer;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

/**
 * @author Abhijit Sarkar
 */
public interface CouchbaseBeerRepository extends CouchbaseRepository<Beer, String> {
}
