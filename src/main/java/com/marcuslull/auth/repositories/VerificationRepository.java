package com.marcuslull.auth.repositories;

import com.marcuslull.auth.models.Verification;
import org.springframework.data.repository.CrudRepository;

public interface VerificationRepository extends CrudRepository<Verification, String> {
}