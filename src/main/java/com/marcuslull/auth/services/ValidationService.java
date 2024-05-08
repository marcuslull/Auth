package com.marcuslull.auth.services;

import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.models.User;
import com.marcuslull.auth.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ValidationService {
    private final UserRepository userRepository;

    public ValidationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Map<String, String> validateRegistration(Registration registration) {

        // response will contain a "message" and an HTML "page"
        Map<String, String> returnMap = new HashMap<>();

        if (requiredFieldsAreNotBlank(registration)) {
            log.warn("REGISTRATION: RegistrationService.registrationProcess(email: {}, password: [PROTECTED]) - Required field is blank", registration.email());
            returnMap.put("message", "Required field is blank!");
            returnMap.put("page", "register");
            return returnMap;
        }

        if (passwordsDontMatch(registration)) {
            log.warn("REGISTRATION: RegistrationService.registrationProcess(email: {}, password: [PROTECTED]) - Passwords must match", registration.email());
            returnMap.put("message", "Passwords must match!");
            returnMap.put("page", "register");
            return returnMap;
        }

        if (passwordIsNotStrong(registration)) {
            log.warn("REGISTRATION: RegistrationService.registrationProcess(email: {}, password: [PROTECTED]) - password is not strong", registration.email());
            returnMap.put("message", "Password is not strong!");
            returnMap.put("page", "register");
            return returnMap;
        }

        if (userExists(registration)) {
            log.warn("REGISTRATION: RegistrationService.registrationProcess(email: {}, password: [PROTECTED]) - user already exists", registration.email());
            returnMap.put("message", "User already exists!");
            returnMap.put("page", "register");
            return returnMap;
        }
        return returnMap;
    }

    public Map<String, String> validatePasswordReset(Registration registration) {
        // response will contain a "message"
        Map<String, String> returnMap = new HashMap<>();

        if (passwordsDontMatch(registration)) {
            log.warn("REGISTRATION: RegistrationService.resetValidate() - Passwords must match");
            returnMap.put("message", "Passwords must match!");
            return returnMap;
        }

        if (passwordIsNotStrong(registration)) {
            log.warn("REGISTRATION: RegistrationService.resetValidate() - password is not strong");
            returnMap.put("message", "Password is not strong!");
            return returnMap;
        }
        return returnMap;
    }

    private boolean requiredFieldsAreNotBlank(Registration registration) {
        return registration.email().isBlank() || registration.password().isBlank() || registration.confirmPassword().isBlank();
    }

    private boolean passwordsDontMatch(Registration registration) {
        return !registration.password().trim().equals(registration.confirmPassword().trim());
    }

    private boolean passwordIsNotStrong(Registration registration) {
        String STRONG_PASSWORD_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&;,]){12,}.*";
        return !Pattern.matches(STRONG_PASSWORD_PATTERN, registration.password().trim());
    }

    private boolean userExists(Registration registration) {
        return userRepository.existsByUsername(registration.email());
    }

    public boolean emailIsWellFormed(User user) {
        String EMAIL_VERIFICATION_PATTERN = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
        return Pattern.matches(EMAIL_VERIFICATION_PATTERN, user.getUsername().trim());
    }

    public boolean codeIsNotExpired(Instant created){
        Instant now = Instant.now();
        Duration duration = Duration.between(created, now);
        return duration.getSeconds() <= 3600; // 1 hour expiration
//        return duration.getSeconds() <= 10; // 10 seconds
    }
}
