package com.marcuslull.auth.services;

import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.models.User;
import com.marcuslull.auth.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.regex.Pattern;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean passwordsMatch(Registration registration) {
        return registration.password().trim().equals(registration.confirmPassword().trim());
    }

    public boolean passwordIsStrong(Registration registration) {
        return Pattern.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&;,]){12,}.*",
                registration.password().trim());
    }

    public boolean userExists(Registration registration) {
        return userRepository.existsByUsername(registration.email());
    }

    public void registerNewUser(Registration registration) {
        User user = new User();
        user.setUsername(registration.email());
        user.setPassword(passwordEncoder.encode(registration.password()));
        user.setEnabled(true);
        user.setGrantedAuthority(Collections.singletonList(new SimpleGrantedAuthority("USER")));
        userRepository.save(user);
    }
}
