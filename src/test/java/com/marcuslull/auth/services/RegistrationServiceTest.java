package com.marcuslull.auth.services;


import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.models.User;
import com.marcuslull.auth.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private VerificationService verificationService;
    @Mock
    private ValidationService validationService;
    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void registerNewUserTest() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        doNothing().when(verificationService).verificationCodeGenerator(any(User.class), anyBoolean());

        registrationService.registerNewUser(new Registration("email", "pass", "pass", null, false));

        verify(passwordEncoder, atLeastOnce()).encode(anyString());
        verify(userRepository, atLeastOnce()).save(any(User.class));
        verify(verificationService, atLeastOnce()).verificationCodeGenerator(any(User.class), anyBoolean());
    }

    @Test
    void registerNewPasswordTest() {
        Map<String, String> comparison = new HashMap<>(Map.of("message", "A password reset will be emailed to you soon"));
        when(userRepository.getUserByUsername(anyString())).thenReturn(Optional.of(new User()));
        doNothing().when(verificationService).verificationCodeGenerator(any(User.class), anyBoolean());

        Map<String, String> result = registrationService.registerNewPassword(new Registration("email", "pass", "pass", null, false));

        assertEquals(comparison.get("message"), result.get("message"));
    }

    @Test
    void registerNewPasswordUserNotFoundTest() {
        Map<String, String> comparison = new HashMap<>(Map.of("message", "A password reset will be emailed to you soon"));
        when(userRepository.getUserByUsername(anyString())).thenReturn(Optional.empty());

        Map<String, String> result = registrationService.registerNewPassword(new Registration("email", "pass", "pass", null, false));

        assertEquals(comparison.get("message"), result.get("message"));
    }
}