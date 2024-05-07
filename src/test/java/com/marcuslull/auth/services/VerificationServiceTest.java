package com.marcuslull.auth.services;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.marcuslull.auth.models.User;
import com.marcuslull.auth.models.Verification;
import com.marcuslull.auth.repositories.UserRepository;
import com.marcuslull.auth.repositories.VerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class VerificationServiceTest {
    @Mock
    private VerificationRepository verificationRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private VerificationService verificationService;



    // the captured log will be stored here for the comparison
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // create a context and get the logger from the applicable module
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger eventLogger =
                loggerContext.getLogger("com.marcuslull.auth.services.VerificationService");

        // add the captured log to the list
        listAppender = new ListAppender<>();
        listAppender.start();
        eventLogger.addAppender(listAppender);
    }

//    @Test
//    void frontSideVerifyPatternMatchFailure() {
//        // arrange
//        User invalidEmailUser = new User();
//        invalidEmailUser.setUsername("badEmailFormat");
//
//        // act
//        verificationService.frontSideVerify(invalidEmailUser);
//
//        // assert
//        verify(verificationRepository, never()).save(any(Verification.class));
//        ILoggingEvent loggingEvent = listAppender.list.getFirst();
//        assertEquals("REGISTRATION: VerificationService.frontSideVerify(user: badEmailFormat) - Invalid email", loggingEvent.getFormattedMessage());
//        assertEquals(Level.WARN, loggingEvent.getLevel());
//    }

//    @Test
//    void frontSideVerifyPatternMatchSuccessAndSendVerificationEmailCalled() {
//        // arrange
//        User validEmailUser = new User();
//        validEmailUser.setUsername("email@email.com");
//        when(verificationRepository.save(any(Verification.class))).thenReturn(new Verification());
//
//        // act
//        verificationService.frontSideVerify(validEmailUser);
//        // assert
//        verify(verificationRepository, atLeastOnce()).save(any(Verification.class));
//        ILoggingEvent loggingEvent = listAppender.list.get(1); // we are looking for the 2nd log message here
//        assertEquals("REGISTRATION: VerificationService.sendVerificationEmail(email: email@email.com)", loggingEvent.getFormattedMessage());
//        assertEquals(Level.WARN, loggingEvent.getLevel());
//    }

    //@Test // TODO: This test does not work correctly because of something with the system clock.
    void backSideVerifySuccess() {
        // arrange
        Clock clock = Clock.systemUTC();
        User user = new User();
        String uuid = UUID.randomUUID().toString();
        Instant created = Instant.now(clock).minus(Duration.ofSeconds(100));
        Verification validVerification = new Verification(uuid, user, created);
        when(verificationRepository.findByCode(anyString())).thenReturn(Optional.of(validVerification));
        when(userRepository.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(verificationRepository).delete(any(Verification.class));

        // act
        boolean result = verificationService.backSideVerify(uuid);


        // assert
        assertTrue(result);
    }

    //@Test // TODO: This test does not work correctly because of something with the system clock.
    void backSideVerifyFailureVerificationNotFound() {

    }

    //@Test // TODO: This test does not work correctly because of something with the system clock.
    void backSideVerifyCodeExpired() {

    }

    //@Test // TODO: This test does not work correctly because of something with the system clock.
    void backSideVerifyUserNotFound() {

    }
}