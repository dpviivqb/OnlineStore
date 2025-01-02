package com.comp5348.storeapp.repository;

import com.comp5348.storeapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<Object> findByEmail(String email);

}
