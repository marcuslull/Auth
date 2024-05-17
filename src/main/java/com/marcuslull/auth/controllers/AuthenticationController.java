package com.marcuslull.auth.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Slf4j
@Controller
public class AuthenticationController {

    @ModelAttribute("isAnon")
    public void isAnon(Principal principal, Model model) {
        model.addAttribute("isAnon", principal == null);
    }

    @GetMapping("/login")
    public String displayLogin(HttpServletRequest request) {
        log.warn("AUTH_REQUEST: AuthenticationController.displayLogin() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        // logic is handled by Spring Security via SecurityConfiguration
        return "login";
    }

    @PostMapping("/login")
    public void postLogin(HttpServletRequest request) {
        log.warn("AUTH_REQUEST: AuthenticationController.postLogin() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        // logic is handled by Spring Security via SecurityConfiguration
    }

    @GetMapping("/logout")
    public String displayLogout(HttpServletRequest request) {
        log.warn("AUTH_REQUEST: AuthenticationController.getLogout() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        return "logout";
    }

    @PostMapping("/logout")
    public void postLogout() {
        // logic is handled by Spring Security via SecurityConfiguration
    }

    @GetMapping("/success")
    public String displaySuccess(HttpServletRequest request) {
        log.warn("AUTH_REQUEST: AuthenticationController.getSuccess() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        return "success";
    }
}
