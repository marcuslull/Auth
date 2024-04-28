package com.marcuslull.auth.listeners;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.marcuslull.auth.configurations.listeners.AuthenticationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AuthenticationEventTest {

    // STRATEGY - The most direct route to test logs seems to be a comparison, so we will capture the log as it fires
    // and compare it to what was outputted by the methods under test.

    @Mock
    private AuthenticationSuccessEvent authenticationSuccessEvent;

    @Mock
    private AbstractAuthenticationFailureEvent abstractAuthenticationFailureEvent;

    @InjectMocks
    private AuthenticationEvent authenticationEvent;

    // the captured log will be stored here for the comparison
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        // fire off the event
        authenticationEvent = new AuthenticationEvent();

        // create a context and get the logger from the applicable module
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger eventLogger =
                loggerContext.getLogger("com.marcuslull.auth.configurations.listeners.AuthenticationEvent");

        // add the captured log to the list
        listAppender = new ListAppender<>();
        listAppender.start();
        eventLogger.addAppender(listAppender);
    }

    @Test
    void onSuccess() {
        // arrange

        // act
        authenticationEvent.onSuccess(authenticationSuccessEvent);

        // assert
        ILoggingEvent loggingEvent = listAppender.list.getFirst();
        assertEquals("EVENT: AuthenticationEvent.onSuccess(authenticationSuccessEvent)", loggingEvent.getFormattedMessage());
        assertEquals(Level.WARN, loggingEvent.getLevel());
    }

    @Test
    void onFailure() {
        // arrange

        // act
        authenticationEvent.onFailure(abstractAuthenticationFailureEvent);

        // assert
        ILoggingEvent loggingEvent = listAppender.list.getFirst();
        assertEquals("EVENT: AuthenticationEvent.onFailure(abstractAuthenticationFailureEvent)", loggingEvent.getFormattedMessage());
        assertEquals(Level.WARN, loggingEvent.getLevel());
    }
}