package name.abhijitsarkar.javaee.salon.user.repository;

import java.util.Optional;

import name.abhijitsarkar.javaee.salon.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
	public Page<User> findByFirstName(@Param("firstName") String firstName, Pageable p);

	public Page<User> findByLastName(@Param("lastName") String lastName, Pageable p);

	public Page<User> findByFirstNameAndLastName(@Param("firstName") String firstName,
			@Param("lastName") String lastName, Pageable p);

	public Page<User> findByEmail(@Param("email") Optional<String> email, Pageable p);

	public Page<User> findByPhoneNum(@Param("phoneNum") String phoneNum, Pageable p);

	public Page<User> findByPhoneNumEndingWith(@Param("phoneNum") String phoneNum, Pageable p);

}
