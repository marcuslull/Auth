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
import java.util.Optional;

@Slf4j
@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    public final ValidationService validationService;

    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationService verificationService,
                               ValidationService validationService) {
        this.verificationService = verificationService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
        log.info("START: RegistrationService");
    }

    public void registerNewUser(Registration registration) {
        User user = new User();
        user.setUsername(registration.email());
        user.setPassword(passwordEncoder.encode(registration.password()));
        user.setEnabled(false);
        user.setGrantedAuthority(Collections.singletonList(new SimpleGrantedAuthority("USER")));
        User savedUser = userRepository.save(user);
        log.warn("REGISTRATION: RegistrationService.registerNewUser(email: {}, password: [PROTECTED]) - new user registered", savedUser.getUsername());
        verificationService.verificationCodeGenerator(savedUser, false);
    }

    public Map<String, String> registerNewPassword(Registration registration) {
        Map<String, String> returnMap = new HashMap<>();
        Optional<User> optionalUser = userRepository.getUserByUsername(registration.email());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            verificationService.verificationCodeGenerator(user, true);
            returnMap.put("message", "A password reset will be emailed to you soon");
            return returnMap;
        }
        log.warn("REGISTRATION: RegistrationService.resetPassword({}) - User was not found, dropping the call", registration.email());
        returnMap.put("message", "A password reset will be emailed to you soon");
        return returnMap;
    }
}
