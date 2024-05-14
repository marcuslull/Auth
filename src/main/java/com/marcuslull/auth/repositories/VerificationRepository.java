package com.marcuslull.auth.repositories;

import com.marcuslull.auth.models.User;
import com.marcuslull.auth.models.Verification;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerificationRepository extends CrudRepository<Verification, String> {
    Optional<Verification> findByCode(String code);
    void deleteAllById(User id);
}