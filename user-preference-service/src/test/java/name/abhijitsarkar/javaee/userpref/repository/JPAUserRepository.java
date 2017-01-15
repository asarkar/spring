package name.abhijitsarkar.javaee.userpref.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import name.abhijitsarkar.javaee.userpref.domain.UserEntity;

public interface JPAUserRepository extends Repository<UserEntity, String> {

    Optional<UserEntity> findOne(String username);
}
