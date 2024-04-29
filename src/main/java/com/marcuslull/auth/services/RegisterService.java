package com.marcuslull.auth.services;

import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.models.User;
import com.marcuslull.auth.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        log.info("START: RegisterService");
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, String> registrationProcess(Registration registration) {

        // Will contain a "message" and an HTML "page"
        Map<String, String> returnMap = new HashMap<>();

        if (!passwordsMatch(registration)) {
            log.warn("REGISTRATION: MainController.postRegister(email: {}, password: [PROTECTED]) - Passwords must match", registration.email());
            returnMap.put("message", "Passwords must match!");
            returnMap.put("page", "register");
            return returnMap;
        }

        if (!passwordIsStrong(registration)) {
            log.warn("REGISTRATION: MainController.passwordIsStrong(email: {}, password: [PROTECTED]) - password is not strong", registration.email());
            returnMap.put("message", "Password is not strong!");
            returnMap.put("page", "register");
            return returnMap;
        }

        if (userExists(registration)) {
            log.warn("REGISTRATION: MainController.userExists(email: {}, password: [PROTECTED]) - user already exists", registration.email());
            returnMap.put("message", "User already exists!");
            returnMap.put("page", "reset");
            return returnMap;
        }

        log.warn("REGISTRATION: MainController.registerNewUser(email: {}, password: [PROTECTED]) - new user registered", registration.email());
        registerNewUser(registration);
        returnMap.put("message", "Success!");
        returnMap.put("page", "redirect:/login");
        return returnMap;
    }

    private boolean passwordsMatch(Registration registration) {
        return registration.password().trim().equals(registration.confirmPassword().trim());
    }

    private boolean passwordIsStrong(Registration registration) {
        return Pattern.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&;,]){12,}.*",
                registration.password().trim());
    }

    private boolean userExists(Registration registration) {
        return userRepository.existsByUsername(registration.email());
    }

    private void registerNewUser(Registration registration) {
        User user = new User();
        user.setUsername(registration.email());
        user.setPassword(passwordEncoder.encode(registration.password()));
        user.setEnabled(true);
        user.setGrantedAuthority(Collections.singletonList(new SimpleGrantedAuthority("USER")));
        userRepository.save(user);
    }
}
