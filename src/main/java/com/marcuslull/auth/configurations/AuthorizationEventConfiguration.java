package com.marcuslull.auth.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;

@Slf4j
@Configuration
public class AuthorizationEventConfiguration {

    @Bean
    public AuthorizationEventPublisher authorizationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        // listener for authorization events
        log.info("AUTH_START: AuthorizationEventConfiguration.authorizationEventPublisher()");
        return new SpringAuthorizationEventPublisher(applicationEventPublisher);
    }
}