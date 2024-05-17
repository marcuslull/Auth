package com.marcuslull.auth.services;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

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
        assertEquals("AUTH_EMAIL: EmailService.sendEmail(email: to link: code) - Success, message sent", loggingEvent.getFormattedMessage());
        assertEquals(Level.WARN, loggingEvent.getLevel());
    }

    @Test
    void sendMailResetTest() {
        emailService.sendEmail("to", "code", true);
        verify(javaMailSender, atLeastOnce()).send(any(SimpleMailMessage.class));

        ILoggingEvent loggingEvent = listAppender.list.get(1);
        assertEquals("AUTH_EMAIL: EmailService.sendEmail(email: to link: code) - Success, message sent", loggingEvent.getFormattedMessage());
        assertEquals(Level.WARN, loggingEvent.getLevel());
    }
}