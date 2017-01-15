package name.abhijitsarkar.javaee.hello.repository;

import name.abhijitsarkar.javaee.hello.domain.CountryLanguage;
import name.abhijitsarkar.javaee.hello.domain.CountryLanguageId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

/**
 * @author Abhijit Sarkar
 */
public interface CountryLanguageRepository extends Repository<CountryLanguage, CountryLanguageId> {
    Page<CountryLanguage> findAll(Pageable pageable);
}
