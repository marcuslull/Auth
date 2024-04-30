package com.marcuslull.auth.controllers;

import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.services.RegisterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Slf4j
@Controller
public class MainController {

    private final RegisterService registerService;

    public MainController(RegisterService registerService) {
        this.registerService = registerService;
        log.info("START: MainController");
    }

    @GetMapping("/")
    public String getIndex(HttpServletRequest request) {
        log.info("REQUEST: MainController.getIndex() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        return "index";
    }

    @GetMapping("/register")
    public String getRegister(HttpServletRequest request) {
        log.info("REQUEST: MainController.getRegister() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        return "register";
    }

    @PostMapping("/register")
    public String postRegister(Registration registration, Model model, HttpServletRequest request) {
        log.info("REQUEST: MainController.postRegister() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        Map<String, String> returnedMap = registerService.registrationProcess(registration);
        if (!returnedMap.containsKey("message") || !returnedMap.containsKey("page")) {
            throw new RuntimeException("Register service returned malformed instructions");
        }
        model.addAttribute("message", returnedMap.get("message"));
        return returnedMap.get("page");
    }

    @GetMapping("/reset")
    public String getReset(HttpServletRequest request) {
        log.info("REQUEST: MainController.getReset() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        return "reset";
    }
}
