package com.marcuslull.auth.repositories;

import com.marcuslull.auth.models.Redirect;
import org.springframework.data.repository.CrudRepository;

public interface RedirectRepository extends CrudRepository<Redirect, Long> {
}