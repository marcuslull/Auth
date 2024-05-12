package com.marcuslull.auth.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Slf4j
@Controller
public class AuthenticationController {

    @GetMapping("/login")
    public String getLogin(HttpServletRequest request, Model model, Principal principal) {
        log.warn("REQUEST: AuthenticationController.getLogin() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        if (principal == null) {
            model.addAttribute("isAnon", true);
        } else { model.addAttribute("isAnon", false); }
        return "login";
    }

    @PostMapping("/login")
    public void postLogin() {
    }

    @GetMapping("/logout")
    public String getLogout(HttpServletRequest request, Model model, Principal principal) {
        log.warn("REQUEST: AuthenticationController.getLogout() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        if (principal == null) {
            model.addAttribute("isAnon", true);
        } else { model.addAttribute("isAnon", false); }
        return "logout";
    }

    @PostMapping("/logout")
    public void postLogout() {
    }

    @GetMapping("/success")
    public String getSuccess(HttpServletRequest request, Model model, Principal principal) {
        log.warn("REQUEST: AuthenticationController.getSuccess() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        if (principal == null) {
            model.addAttribute("isAnon", true);
        } else { model.addAttribute("isAnon", false); }
        return "success";
    }
}
