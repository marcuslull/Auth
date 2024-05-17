package com.marcuslull.auth.configurations.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.authorization.event.AuthorizationGrantedEvent;

@Slf4j
@Configuration
public class AuthorizationEvent {

    @Bean
    public AuthorizationEventPublisher authorizationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        // listener for authorization events
        log.info("AUTH_START: AuthorizationEvent.authorizationEventPublisher()");
        return new SpringAuthorizationEventPublisher(applicationEventPublisher);
    }

    @EventListener
    public void onSuccess(AuthorizationGrantedEvent<AuthorizationEvent> grantedEvent) {
        log.warn("AUTH_EVENT: AuthorizationEvent.onSuccess({})", grantedEvent);
    }

    @EventListener
    public void onFailure(AuthorizationDeniedEvent<AuthorizationEvent> deniedEvent) {
        log.warn("AUTH_EVENT: AuthorizationEvent.onFailure({})", deniedEvent);
    }
}
