package com.marcuslull.auth.controllers;

import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.services.RegisterService;
import com.marcuslull.auth.services.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Controller
public class MainController {

    private final RegisterService registerService;
    private final VerificationService verificationService;

    public MainController(RegisterService registerService, VerificationService verificationService) {
        this.registerService = registerService;
        this.verificationService = verificationService;
        log.info("START: MainController");
    }

    @GetMapping("/")
    public String getIndex(HttpServletRequest request, Model model, Principal principal) {
        log.info("REQUEST: MainController.getIndex() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        // anonymous check
        if (principal == null) {
            model.addAttribute("isAnon", true);
        } else { model.addAttribute("isAnon", false); }
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

    @GetMapping("/verify")
    public String getVerify(HttpServletRequest request, @RequestParam(name = "code", required = false) String code, Model model) {
        log.info("REQUEST: MainController.getVerify() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        log.info("REQUEST: MainController.getVerify() - Request parameter: code={}", code);

        // redirect if no code
        if (code == null) {
            return "index";
        }

        // happy path
        if (verificationService.backSideVerify(code)) {
            model.addAttribute("isSuccess", true);
            return "verify";
        }

        registerService.resendVerificationCode(code);
        model.addAttribute("isSuccess", false);
        return "verify";
    }
}
