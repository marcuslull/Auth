package com.marcuslull.auth.repositories;

import com.marcuslull.auth.models.Client;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClientRepository extends CrudRepository<Client, Long> {
    Optional<Client> findByClientId(String clientId);
}
