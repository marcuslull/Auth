package com.marcuslull.auth.controllers;

import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.services.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class MainController {

    private final RegisterService registerService;

    public MainController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @GetMapping("/")
    public String getIndex() {
        return "index";
    }

    @GetMapping("/register")
    public String getRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String postRegister(Registration registration, Model model) {

        if (!registerService.passwordsMatch(registration)) {
            model.addAttribute("errorMessage", "Passwords must match!");
            log.warn("REGISTRATION: MainController.postRegister(email: {}, password: [PROTECTED]) - Passwords must match", registration.email());
            return "register";
        }
        if (!registerService.passwordIsStrong(registration)) {
            model.addAttribute("errorMessage", "Password is not strong!");
            log.warn("REGISTRATION: MainController.passwordIsStrong(email: {}, password: [PROTECTED]) - password is not strong", registration.email());
            return "register";
        }
        if (registerService.userExists(registration)) {
            model.addAttribute("errorMessage", "User already exists!");
            log.warn("REGISTRATION: MainController.userExists(email: {}, password: [PROTECTED]) - user already exists", registration.email());
            return "reset";
        }
        registerService.registerNewUser(registration);
        log.warn("REGISTRATION: MainController.registerNewUser(email: {}, password: [PROTECTED]) - new user registered", registration.email());
        return "redirect:/login"; // TODO: redirect to login page
    }
}
