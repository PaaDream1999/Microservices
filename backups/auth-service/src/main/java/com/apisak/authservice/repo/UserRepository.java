// src/main/java/com/apisak/authservice/repo/UserRepository.java
package com.apisak.authservice.repo;

import com.apisak.authservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}