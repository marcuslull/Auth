package com.marcuslull.auth.services;


import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.models.User;
import com.marcuslull.auth.models.Verification;
import com.marcuslull.auth.repositories.UserRepository;
import com.marcuslull.auth.repositories.VerificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private VerificationRepository verificationRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ValidationService validationService;
    @InjectMocks
    private VerificationService verificationService;

    @Test
    void verificationProcessorPasswordResetTest() {
        Registration resetRegistration = new Registration("", "password", "password", "", true);
        User resetUser = new User(1L, "email", "password", true, List.of(new SimpleGrantedAuthority("USER")));
        Verification resetVerification = new Verification("randomUUID", resetUser, Instant.now());
        when(verificationRepository.findByCode(anyString())).thenReturn(Optional.of(resetVerification));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(resetUser));
        when(validationService.codeIsNotExpired(any(Instant.class))).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(resetUser);
        doNothing().when(verificationRepository).delete(any(Verification.class));

        boolean result = verificationService.verificationCodeProcessor("randomUUID", resetRegistration);

        verify(passwordEncoder, atLeastOnce()).encode(anyString());
        verify(userRepository, atLeastOnce()).save(any(User.class));
        verify(verificationRepository, atLeastOnce()).delete(any(Verification.class));
        assertTrue(result);
    }

    @Test
    void verificationProcessorNewRegistrationTest() {
        Registration newRegistration = new Registration("", "password", "password", "", false);
        User newUser = new User(1L, "email", "password", false, List.of(new SimpleGrantedAuthority("USER")));
        Verification newVerification = new Verification("randomUUID", newUser, Instant.now());
        when(verificationRepository.findByCode(anyString())).thenReturn(Optional.of(newVerification));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(newUser));
        when(validationService.codeIsNotExpired(any(Instant.class))).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        doNothing().when(verificationRepository).delete(any(Verification.class));

        boolean result = verificationService.verificationCodeProcessor("randomUUID", newRegistration);

        verify(userRepository, atLeastOnce()).save(any(User.class));
        verify(verificationRepository, atLeastOnce()).delete(any(Verification.class));
        assertTrue(result);
    }

    @Test
    void verificationProcessorExpiredCodeTest() {
        Registration newRegistration = new Registration("", "password", "password", "", false);
        User newUser = new User(1L, "email", "password", false, List.of(new SimpleGrantedAuthority("USER")));
        Verification expiredVerification = new Verification("randomUUID", newUser, Instant.now());
        Verification newVerification = new Verification("randomUUID", newUser, Instant.now());
        when(verificationRepository.findByCode(anyString())).thenReturn(Optional.of(expiredVerification));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(newUser));
        when(validationService.codeIsNotExpired(any(Instant.class))).thenReturn(false);
        when(validationService.emailIsWellFormed(any(User.class))).thenReturn(true);
        when(verificationRepository.save(any(Verification.class))).thenReturn(newVerification);
        doNothing().when(emailService).sendEmail(anyString(),anyString(),anyBoolean());
        doNothing().when(verificationRepository).delete(any(Verification.class));

        boolean result = verificationService.verificationCodeProcessor("randomUUID", newRegistration);

        verify(validationService, atLeastOnce()).emailIsWellFormed(any(User.class));
        verify(verificationRepository, atLeastOnce()).save(any(Verification.class));
        verify(emailService, atLeastOnce()).sendEmail(anyString(),anyString(),anyBoolean());
        verify(verificationRepository, atLeastOnce()).delete(any(Verification.class));
        assertTrue(result);
    }

    @Test
    void verificationCodeGeneratorTest() {
        User newUser = new User(1L, "email", "password", false, List.of(new SimpleGrantedAuthority("USER")));
        Verification newVerification = new Verification("randomUUID", newUser, Instant.now());
        when(validationService.emailIsWellFormed(any(User.class))).thenReturn(true);
        when(verificationRepository.save(any(Verification.class))).thenReturn(newVerification);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyBoolean());

        verificationService.verificationCodeGenerator(newUser, false);

        verify(validationService, atLeastOnce()).emailIsWellFormed(any(User.class));
        verify(verificationRepository, atLeastOnce()).save(any(Verification.class));
        verify(emailService, atLeastOnce()).sendEmail(anyString(),anyString(),anyBoolean());
    }

    @Test
    void verificationCodeGeneratorInvalidEmailTest() {
        User newUser = new User(1L, "email", "password", false, List.of(new SimpleGrantedAuthority("USER")));
        when(validationService.emailIsWellFormed(any(User.class))).thenReturn(false);

        verificationService.verificationCodeGenerator(newUser, false);

        verify(validationService, atLeastOnce()).emailIsWellFormed(any(User.class));
        verify(verificationRepository, never()).save(any(Verification.class));
        verify(emailService, never()).sendEmail(anyString(),anyString(),anyBoolean());
    }
}