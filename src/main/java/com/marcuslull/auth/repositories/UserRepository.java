package com.marcuslull.auth.repositories;

import com.marcuslull.auth.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    public Optional<User> getUserByUsername(String username);
}