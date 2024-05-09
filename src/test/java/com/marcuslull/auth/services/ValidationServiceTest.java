package com.marcuslull.auth.services;


import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.models.User;
import com.marcuslull.auth.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    ValidationService validationService;

    Registration validRegistration = new Registration(
            "email@email.com",
            "Password123!",
            "Password123!",
            null,
            false);

    Registration invalidPasswordStrengthRegistration = new Registration(
            "email@email.com",
            "password!0A",
            "password!0A",
            null,
            false);

    Registration invalidMisMatchPasswordRegistration = new Registration(
            "email@email.com",
            "Password123!",
            "Password126!",
            null,
            false);

    Registration invalidFieldBlankRegistration = new Registration(
            "email@email.com",
            "Password123!",
            "",
            null,
            false);

    @Test
    void validateRegistrationTest() {
        Map<String, String> result = validationService.validateRegistration(validRegistration);

        assertEquals(0, result.size());
    }

    @Test
    void validateRegistrationFieldBlankTest() {
        Map<String, String> comparison = new HashMap<>(Map.of("message", "Required field is blank!","page", "register"));
        Map<String, String> result = validationService.validateRegistration(invalidFieldBlankRegistration);

        assertEquals(2, result.size());
        assertEquals(comparison.get("message"), result.get("message"));
        assertEquals(comparison.get("page"), result.get("page"));
    }

    @Test
    void validateRegistrationMisMatchedPasswordsTest() {
        Map<String, String> comparison = new HashMap<>(Map.of("message", "Passwords must match!","page", "register"));
        Map<String, String> result = validationService.validateRegistration(invalidMisMatchPasswordRegistration);

        assertEquals(2, result.size());
        assertEquals(comparison.get("message"), result.get("message"));
        assertEquals(comparison.get("page"), result.get("page"));
    }

    @Test
    void validateRegistrationPasswordStrengthTest() {
        Map<String, String> comparison = new HashMap<>(Map.of("message", "Password is not strong!","page", "register"));
        Map<String, String> result = validationService.validateRegistration(invalidPasswordStrengthRegistration);

        assertEquals(2, result.size());
        assertEquals(comparison.get("message"), result.get("message"));
        assertEquals(comparison.get("page"), result.get("page"));
    }

    @Test
    void validatePasswordResetTest() {
        Map<String, String> result = validationService.validatePasswordReset(validRegistration);

        assertEquals(0, result.size());
    }

    @Test
    void validatePasswordResetMisMatchedPasswordsTest() {
        Map<String, String> comparison = new HashMap<>(Map.of("message", "Passwords must match!","page", "register"));
        Map<String, String> result = validationService.validatePasswordReset(invalidMisMatchPasswordRegistration);

        assertEquals(1, result.size());
        assertEquals(comparison.get("message"), result.get("message"));
    }

    @Test
    void validatePasswordResetPasswordStrengthTest() {
        Map<String, String> comparison = new HashMap<>(Map.of("message", "Password is not strong!","page", "register"));
        Map<String, String> result = validationService.validatePasswordReset(invalidPasswordStrengthRegistration);

        assertEquals(1, result.size());
        assertEquals(comparison.get("message"), result.get("message"));
    }

    @Test
    void emailIsWellFormedTest() {
        User user = new User();
        user.setUsername("email@email.com");

        boolean result = validationService.emailIsWellFormed(user);

        assertTrue(result);
    }

    @Test
    void emailIsNotWellFormedTest() {
        User user = new User();
        user.setUsername("email@.org");

        boolean result = validationService.emailIsWellFormed(user);

        assertFalse(result);
    }

    @Test
    void codeIsNotExpiredTest() {
        boolean result = validationService.codeIsNotExpired(Instant.now());

        assertTrue(result);
    }

    @Test
    void codeIsExpiredTest() {
        boolean result = validationService.codeIsNotExpired(Instant.now().minusSeconds(3601));

        assertFalse(result);
    }
}