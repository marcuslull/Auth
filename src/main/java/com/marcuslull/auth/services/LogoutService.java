package com.marcuslull.auth.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class LogoutService {

    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // the security context logout handler will properly log the user out according to Spring security settings and Security configuration
        if (request != null && response != null && authentication != null) {
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            log.warn("AUTH_LOGOUT: LogoutService.logout({}) - Programmatic logout", authentication.getName());
            logoutHandler.logout(request, response, authentication);
        }
    }
}
