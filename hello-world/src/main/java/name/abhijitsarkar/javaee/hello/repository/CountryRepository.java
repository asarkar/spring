package name.abhijitsarkar.javaee.hello.repository;

import name.abhijitsarkar.javaee.hello.domain.City;
import name.abhijitsarkar.javaee.hello.domain.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;

/**
 * @author Abhijit Sarkar
 */
public interface CountryRepository extends Repository<Country, String> {
    Page<Country> findAll(Pageable pageable);
}
