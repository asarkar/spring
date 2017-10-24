package org.abhijitsarkar.spring.beer.repository;

import org.abhijitsarkar.spring.beer.domain.Beer;
import org.springframework.stereotype.Repository;

/**
 * @author Abhijit Sarkar
 */
@Repository
public class CouchbaseBeerRepository extends BaseCouchbaseRepository<Beer> {
}
