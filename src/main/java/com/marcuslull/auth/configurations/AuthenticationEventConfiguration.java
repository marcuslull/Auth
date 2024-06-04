package com.marcuslull.auth.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;

@Slf4j
@Configuration
public class AuthenticationEventConfiguration {

    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        // listener for authentication events
        log.info("AUTH_START: AuthenticationEventConfiguration.authenticationEventPublisher()");
        return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
    }
}
