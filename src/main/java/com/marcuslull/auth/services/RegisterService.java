package com.marcuslull.auth.services;

import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.models.User;
import com.marcuslull.auth.models.Verification;
import com.marcuslull.auth.repositories.UserRepository;
import com.marcuslull.auth.repositories.VerificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
public class RegisterService {
    private final VerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;

    public RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationService verificationService,
                           VerificationRepository verificationRepository) {
        this.verificationService = verificationService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        log.info("START: RegisterService");
        this.verificationRepository = verificationRepository;
    }

    public void resendVerificationCode(String expiredCode) {
        Optional<Verification> optionalVerification = verificationRepository.findByCode(expiredCode);
        if (optionalVerification.isPresent()) {
            Verification verification = optionalVerification.get();
            Optional<User> optionalUser = userRepository.getUserById(verification.getId().getId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                log.warn("REGISTRATION: RegisterService.resendVerificationCode({}) - Found code and user, handing off to verification service and removing obsolete verification", expiredCode);
                verificationService.frontSideVerify(user);
                verificationRepository.delete(verification);
            } else { log.warn("REGISTRATION: RegisterService.resendVerificationCode({}) - User not found, dropping the call", expiredCode); }
        } else { log.warn("REGISTRATION: RegisterService.resendVerificationCode({}) - Verification not found, dropping the call", expiredCode); }
    }

    public Map<String, String> registrationProcess(Registration registration) {

        // response will contain a "message" and an HTML "page"
        Map<String, String> returnMap = new HashMap<>();

        if (registration.email().isBlank() || registration.password().isBlank() || registration.confirmPassword().isBlank()) {
            log.warn("REGISTRATION: RegisterService.registrationProcess(email: {}, password: [PROTECTED]) - Required field is blank", registration.email());
            returnMap.put("message", "Required field is blank!");
            returnMap.put("page", "register");
            return returnMap;
        }

        if (!passwordsMatch(registration)) {
            log.warn("REGISTRATION: RegisterService.registrationProcess(email: {}, password: [PROTECTED]) - Passwords must match", registration.email());
            returnMap.put("message", "Passwords must match!");
            returnMap.put("page", "register");
            return returnMap;
        }

        if (!passwordIsStrong(registration)) {
            log.warn("REGISTRATION: RegisterService.registrationProcess(email: {}, password: [PROTECTED]) - password is not strong", registration.email());
            returnMap.put("message", "Password is not strong!");
            returnMap.put("page", "register");
            return returnMap;
        }

        if (userExists(registration)) {
            log.warn("REGISTRATION: RegisterService.registrationProcess(email: {}, password: [PROTECTED]) - user already exists", registration.email());
            returnMap.put("message", "User already exists!");
            returnMap.put("page", "register");
            return returnMap;
        }

        registerNewUser(registration);
        returnMap.put("message", "Success - Please check your email for verification link!");
        returnMap.put("page", "register");
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
        user.setEnabled(false);
        user.setGrantedAuthority(Collections.singletonList(new SimpleGrantedAuthority("USER")));
        User savedUser = userRepository.save(user);
        log.warn("REGISTRATION: RegisterService.registerNewUser(email: {}, password: [PROTECTED]) - new user registered", savedUser.getUsername());
        verificationService.frontSideVerify(savedUser);
    }
}
