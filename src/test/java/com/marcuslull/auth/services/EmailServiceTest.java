package com.marcuslull.auth.services;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.read.ListAppender;
import jakarta.mail.internet.MimeMessage;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock
    private JavaMailSender javaMailSender;
    @InjectMocks
    private EmailService emailService;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    public void setUp() throws Exception {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger eventLogger =
                loggerContext.getLogger("com.marcuslull.auth.services.EmailService");

        // add the captured log to the list
        listAppender = new ListAppender<>();
        listAppender.start();
        eventLogger.addAppender(listAppender);
    }

    @Test
    void sendMailTest() {
        emailService.sendEmail("to", "code", false);
        verify(javaMailSender, atLeastOnce()).send(any(SimpleMailMessage.class));

        ILoggingEvent loggingEvent = listAppender.list.get(1);
        assertEquals("EMAIL: EmailService.sendEmail(email: to link: code) - Success, message sent", loggingEvent.getFormattedMessage());
        assertEquals(Level.WARN, loggingEvent.getLevel());
    }

    @Test
    void sendMailResetTest() {
        emailService.sendEmail("to", "code", true);
        verify(javaMailSender, atLeastOnce()).send(any(SimpleMailMessage.class));

        ILoggingEvent loggingEvent = listAppender.list.get(1);
        assertEquals("EMAIL: EmailService.sendEmail(email: to link: code) - Success, message sent", loggingEvent.getFormattedMessage());
        assertEquals(Level.WARN, loggingEvent.getLevel());
    }
}