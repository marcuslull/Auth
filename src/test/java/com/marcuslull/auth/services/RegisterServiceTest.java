package com.marcuslull.auth.services;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.models.User;
import com.marcuslull.auth.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class RegisterServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private VerificationService verificationService;
    @InjectMocks
    private RegisterService registerService;

    // the captured log will be stored here for the comparison
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // create a context and get the logger from the applicable module
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger eventLogger =
                loggerContext.getLogger("com.marcuslull.auth.services.RegisterService");

        // add the captured log to the list
        listAppender = new ListAppender<>();
        listAppender.start();
        eventLogger.addAppender(listAppender);
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
        when(userRepository.save(any())).thenReturn(new User());
        doNothing().when(verificationService).verify(any(User.class));

        // act
        Map<String, String> result = registerService.registrationProcess(registration);

        // assert
        assertEquals("Success - Please check your email for verification link!", result.get("message"));
        assertEquals("register", result.get("page"));
        ILoggingEvent loggingEvent = listAppender.list.getFirst();
        assertEquals("REGISTRATION: RegisterService.registerNewUser(email: null, password: [PROTECTED]) - new user registered", loggingEvent.getFormattedMessage());
        assertEquals(Level.WARN, loggingEvent.getLevel());
    }
}