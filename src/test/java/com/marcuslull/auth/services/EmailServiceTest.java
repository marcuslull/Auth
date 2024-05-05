package com.marcuslull.auth.services;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.marcuslull.auth.models.Verification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {
    @Mock
    private JavaMailSender javaMailSender;
    @InjectMocks
    private EmailService emailService;

    // the captured log will be stored here for the comparison
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // create a context and get the logger from the applicable module
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger eventLogger =
                loggerContext.getLogger("com.marcuslull.auth.services.EmailService");

        // add the captured log to the list
        listAppender = new ListAppender<>();
        listAppender.start();
        eventLogger.addAppender(listAppender);
    }

    @Test
    void sendEmailVerificationSuccess() {
        // arrange

        // act
        emailService.sendEmailVerification("email", "link");

        // assert
        ILoggingEvent loggingEvent = listAppender.list.getFirst();
        assertEquals("REGISTRATION: EmailService.sendEmailVerification(email: email link: link) - Success, message sent", loggingEvent.getFormattedMessage());
        assertEquals(Level.WARN, loggingEvent.getLevel());
    }
    @Test
    void sendEmailVerificationFailure() {
        // arrange
        doThrow(RuntimeException.class).when(javaMailSender).send(any(SimpleMailMessage.class));

        // act & assert
        assertThrows(RuntimeException.class, () -> emailService.sendEmailVerification("email", "link"));
        ILoggingEvent loggingEvent = listAppender.list.getFirst();
        assertEquals("REGISTRATION: EmailService.sendEmailVerification(email: email link: link) - Failure, authentication or network error", loggingEvent.getFormattedMessage());
        assertEquals(Level.WARN, loggingEvent.getLevel());
    }

}