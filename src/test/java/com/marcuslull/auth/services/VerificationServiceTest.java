package com.marcuslull.auth.services;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.marcuslull.auth.models.User;
import com.marcuslull.auth.models.Verification;
import com.marcuslull.auth.repositories.VerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerificationServiceTest {
    @Mock
    private VerificationRepository verificationRepository;
    @Mock
    private EmailService emailService;
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

    @Test
    void verifyPatternMatchFailure() {
        // arrange
        User invalidEmailUser = new User();
        invalidEmailUser.setUsername("badEmailFormat");

        // act
        verificationService.verify(invalidEmailUser);

        // assert
        verify(verificationRepository, never()).save(any(Verification.class));
        ILoggingEvent loggingEvent = listAppender.list.getFirst();
        assertEquals("REGISTRATION: VerificationService.verify(user: badEmailFormat) - Invalid email", loggingEvent.getFormattedMessage());
        assertEquals(Level.WARN, loggingEvent.getLevel());
    }

    @Test
    void verifyPatternMatchSuccessAndSendVerificationEmailCalled() {
        // arrange
        User validEmailUser = new User();
        validEmailUser.setUsername("email@email.com");
        when(verificationRepository.save(any(Verification.class))).thenReturn(new Verification());

        // act
        verificationService.verify(validEmailUser);
        // assert
        verify(verificationRepository, atLeastOnce()).save(any(Verification.class));
        ILoggingEvent loggingEvent = listAppender.list.get(1); // we are looking for the 2nd log message here
        assertEquals("REGISTRATION: VerificationService.sendVerificationEmail(email: email@email.com)", loggingEvent.getFormattedMessage());
        assertEquals(Level.WARN, loggingEvent.getLevel());
    }
}