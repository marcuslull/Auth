package com.marcuslull.auth.services;

import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class RegisterServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private RegisterService registerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegistrationProcessWhenFieldsAreBlank() {
        // arrange
        Registration registration = new Registration("", "", "");

        // act
        Map<String, String> result = registerService.registrationProcess(registration);

        // assert
        assertEquals("Required field is blank!", result.get("message"));
        assertEquals("register", result.get("page"));
    }

    @Test
    public void testRegistrationProcessWhenPasswordsDoNotMatch() {
        // arrange
        Registration registration = new Registration("test@test.com", "password", "differentPassword");

        // act
        Map<String, String> result = registerService.registrationProcess(registration);

        // assert
        assertEquals("Passwords must match!", result.get("message"));
        assertEquals("register", result.get("page"));
    }

    @Test
    public void testRegistrationProcessWhenPasswordIsNotStrong() {
        // arrange
        Registration registration = new Registration("test@test.com", "weakpassword", "weakpassword");

        // act
        Map<String, String> result = registerService.registrationProcess(registration);

        // assert
        assertEquals("Password is not strong!", result.get("message"));
        assertEquals("register", result.get("page"));
    }

    @Test
    public void testRegistrationProcessWhenUserExists() {
        // arrange
        when(userRepository.existsByUsername("test@test.com")).thenReturn(true);
        Registration registration = new Registration("test@test.com", "StrongPassword1@", "StrongPassword1@");

        // act
        Map<String, String> result = registerService.registrationProcess(registration);

        // assert
        assertEquals("User already exists!", result.get("message"));
        assertEquals("register", result.get("page"));
    }

    @Test
    public void testRegistrationProcessWhenRegistrationIsSuccessful() {
        // arrange
        when(userRepository.existsByUsername("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("StrongPassword1@")).thenReturn("encodedPassword");
        Registration registration = new Registration("test@test.com", "StrongPassword1@", "StrongPassword1@");

        // act
        Map<String, String> result = registerService.registrationProcess(registration);

        // assert
        assertEquals("Success!", result.get("message"));
        assertEquals("redirect:/login", result.get("page"));
    }
}