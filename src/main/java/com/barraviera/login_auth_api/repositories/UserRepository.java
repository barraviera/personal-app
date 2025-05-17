package com.barraviera.login_auth_api.repositories;

import com.barraviera.login_auth_api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository<User, String> precisamos passar para o Jpa a entidade e o tipo da chave primaria
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

}
