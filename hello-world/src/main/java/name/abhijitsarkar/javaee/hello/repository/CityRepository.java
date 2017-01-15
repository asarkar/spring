package name.abhijitsarkar.javaee.hello.repository;

import name.abhijitsarkar.javaee.hello.domain.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

/**
 * @author Abhijit Sarkar
 */
public interface CityRepository extends Repository<City, Long> {
    Page<City> findAll(Pageable pageable);
}
