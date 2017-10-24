package org.abhijitsarkar.spring.beer.repository;

import org.abhijitsarkar.spring.beer.domain.Brewery;
import org.springframework.stereotype.Repository;

/**
 * @author Abhijit Sarkar
 */
@Repository
public class CouchbaseBreweryRepository extends BaseCouchbaseRepository<Brewery> {
}
