package com.marcuslull.auth.configurations.listeners;

import com.marcuslull.auth.configurations.AuthorizationEventConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.authorization.event.AuthorizationGrantedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizationEventListener {

    @EventListener
    public void onSuccess(AuthorizationGrantedEvent<AuthorizationEventConfiguration> grantedEvent) {
        log.warn("AUTH_EVENT: AuthorizationEventListener.onSuccess({})", grantedEvent);
    }

    @EventListener
    public void onFailure(AuthorizationDeniedEvent<AuthorizationEventConfiguration> deniedEvent) {
        log.warn("AUTH_EVENT: AuthorizationEventListener.onFailure({})", deniedEvent);
    }
}