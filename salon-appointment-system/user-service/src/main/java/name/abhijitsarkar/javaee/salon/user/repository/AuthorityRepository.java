package name.abhijitsarkar.javaee.salon.user.repository;

import name.abhijitsarkar.javaee.salon.user.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long>, AuthorityRepositoryCustom {
}
