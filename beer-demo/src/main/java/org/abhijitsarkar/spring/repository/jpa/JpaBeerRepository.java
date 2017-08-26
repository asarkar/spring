package org.abhijitsarkar.spring.repository.jpa;

import org.abhijitsarkar.spring.domain.Beer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Abhijit Sarkar
 */
public interface JpaBeerRepository extends JpaRepository<Beer, String> {
}
