package com.marcuslull.auth.configurations.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationEventListener {

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent successEvent) {
        log.warn("AUTH_EVENT: AuthenticationEventListener.onSuccess({} - {})", successEvent.getAuthentication().getName(), successEvent);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failureEvent) {
        log.warn("AUTH_EVENT: AuthenticationEventListener.onFailure({} - {})", failureEvent.getAuthentication().getName(), failureEvent);
    }
}
