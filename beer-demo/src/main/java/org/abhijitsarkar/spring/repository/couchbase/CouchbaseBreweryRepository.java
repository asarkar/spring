package org.abhijitsarkar.spring.repository.couchbase;

import org.abhijitsarkar.spring.domain.Brewery;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

/**
 * @author Abhijit Sarkar
 */
public interface CouchbaseBreweryRepository extends CouchbaseRepository<Brewery, String> {
}
