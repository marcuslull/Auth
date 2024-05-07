package com.marcuslull.auth.services;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.models.User;
import com.marcuslull.auth.models.Verification;
import com.marcuslull.auth.repositories.UserRepository;
import com.marcuslull.auth.repositories.VerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RegistrationServiceTest {

//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private PasswordEncoder passwordEncoder;
//    @Mock
//    private VerificationService verificationService;
//    @Mock
//    private VerificationRepository verificationRepository;
//    @InjectMocks
//    private RegistrationService registrationService;
//
//    // the captured log will be stored here for the comparison
//    private ListAppender<ILoggingEvent> listAppender;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//
//        // create a context and get the logger from the applicable module
//        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
//        ch.qos.logback.classic.Logger eventLogger =
//                loggerContext.getLogger("com.marcuslull.auth.services.RegistrationService");
//
//        // add the captured log to the list
//        listAppender = new ListAppender<>();
//        listAppender.start();
//        eventLogger.addAppender(listAppender);
//    }
//
//    @Test
//    public void testRegistrationProcessWhenFieldsAreBlank() {
//        // arrange
//        Registration registration = new Registration("", "", "");
//
//        // act
//        Map<String, String> result = registrationService.validationService.registrationValidation(registration, registrationService);
//
//        // assert
//        assertEquals("Required field is blank!", result.get("message"));
//        assertEquals("register", result.get("page"));
//    }
//
//    @Test
//    public void testRegistrationProcessWhenPasswordsDoNotMatch() {
//        // arrange
//        Registration registration = new Registration("test@test.com", "password", "differentPassword");
//
//        // act
//        Map<String, String> result = registrationService.validationService.registrationValidation(registration, registrationService);
//
//        // assert
//        assertEquals("Passwords must match!", result.get("message"));
//        assertEquals("register", result.get("page"));
//    }
//
//    @Test
//    public void testRegistrationProcessWhenPasswordIsNotStrong() {
//        // arrange
//        Registration registration = new Registration("test@test.com", "weakpassword", "weakpassword");
//
//        // act
//        Map<String, String> result = registrationService.validationService.registrationValidation(registration, registrationService);
//
//        // assert
//        assertEquals("Password is not strong!", result.get("message"));
//        assertEquals("register", result.get("page"));
//    }
//
//    @Test
//    public void testRegistrationProcessWhenUserExists() {
//        // arrange
//        when(userRepository.existsByUsername("test@test.com")).thenReturn(true);
//        Registration registration = new Registration("test@test.com", "StrongPassword1@", "StrongPassword1@");
//
//        // act
//        Map<String, String> result = registrationService.validationService.registrationValidation(registration, registrationService);
//
//        // assert
//        assertEquals("User already exists!", result.get("message"));
//        assertEquals("register", result.get("page"));
//    }
//
//    @Test
//    public void testRegistrationProcessWhenRegistrationIsSuccessful() {
//        // arrange
//        when(userRepository.existsByUsername("test@test.com")).thenReturn(false);
//        when(passwordEncoder.encode("StrongPassword1@")).thenReturn("encodedPassword");
//        Registration registration = new Registration("test@test.com", "StrongPassword1@", "StrongPassword1@");
//        when(userRepository.save(any())).thenReturn(new User());
//        doNothing().when(verificationService).frontSideVerify(any(User.class));
//
//        // act
//        Map<String, String> result = registrationService.validationService.registrationValidation(registration, registrationService);
//
//        // assert
//        assertEquals("Success - Please check your email for verification link!", result.get("message"));
//        assertEquals("register", result.get("page"));
//        ILoggingEvent loggingEvent = listAppender.list.getFirst();
//        assertEquals("REGISTRATION: RegistrationService.registerNewUser(email: null, password: [PROTECTED]) - new user registered", loggingEvent.getFormattedMessage());
//        assertEquals(Level.WARN, loggingEvent.getLevel());
//    }
//
//    @Test
//    public void resendVerificationCodeSuccess() {
//        // arrange
//        Verification verification = new Verification();
//        User user = new User();
//        user.setId(1L);
//        verification.setId(user);
//        when(verificationRepository.findByCode(anyString())).thenReturn(Optional.of(verification));
//        when(userRepository.getUserById(anyLong())).thenReturn(Optional.of(user));
//        doNothing().when(verificationService).frontSideVerify(any(User.class));
//        doNothing().when(verificationRepository).delete(any(Verification.class));
//
//        //act
//        registrationService.resendVerificationCode("expiredUuid");
//
//        //assert
//        verify(verificationService, atLeastOnce()).frontSideVerify(any(User.class));
//        verify(verificationRepository, atLeastOnce()).delete(any(Verification.class));
//    }
}