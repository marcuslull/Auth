package com.marcuslull.auth.configurations.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

@Slf4j
@Configuration
public class AuthenticationEvent {

    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        // listener for authentication events
        log.info("AUTH_START: AuthenticationEvent.authenticationEventPublisher()");
        return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent successEvent) {
        log.warn("AUTH_EVENT: AuthenticationEvent.onSuccess({} - {})", successEvent.getAuthentication().getName(), successEvent);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failureEvent) {
        log.warn("AUTH_EVENT: AuthenticationEvent.onFailure({} - {})", failureEvent.getAuthentication().getName(), failureEvent);
    }
}
