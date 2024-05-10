package com.marcuslull.auth;

import com.marcuslull.auth.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {


    public DataLoader() {
    }

    @Override
    public void run(String... args) {
    }
}
