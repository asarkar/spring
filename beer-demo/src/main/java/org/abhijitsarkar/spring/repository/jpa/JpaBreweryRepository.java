package org.abhijitsarkar.spring.repository.jpa;

import org.abhijitsarkar.spring.domain.Brewery;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Abhijit Sarkar
 */
public interface JpaBreweryRepository extends JpaRepository<Brewery, String> {
}
