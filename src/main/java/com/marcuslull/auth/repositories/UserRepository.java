package com.marcuslull.auth.repositories;

import com.marcuslull.auth.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> getUserByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> getUserById(Long id);
}