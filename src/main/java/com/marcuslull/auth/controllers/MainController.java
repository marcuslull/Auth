package com.marcuslull.auth.controllers;

import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.services.PasswordResetService;
import com.marcuslull.auth.services.RegistrationService;
import com.marcuslull.auth.services.ValidationService;
import com.marcuslull.auth.services.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Controller
public class MainController {

    private final RegistrationService registrationService;
    private final VerificationService verificationService;
    private final ValidationService validationService;
    private final PasswordResetService passwordResetService;

    public MainController(RegistrationService registrationService, VerificationService verificationService,
                          ValidationService validationService, PasswordResetService passwordResetService) {
        this.registrationService = registrationService;
        this.verificationService = verificationService;
        this.validationService = validationService;
        this.passwordResetService = passwordResetService;
        log.info("AUTH_START: MainController");
    }

    @ModelAttribute("isAnon")
    public void isAnon(Principal principal, Model model) {
        model.addAttribute("isAnon", principal == null);
    }

    @GetMapping("/")
    public String displayIndex(HttpServletRequest request) {
        log.warn("AUTH_REQUEST: MainController.getIndex() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        return "index";
    }

    @GetMapping("/register")
    public String displayRegister(HttpServletRequest request) {
        log.warn("AUTH_REQUEST: MainController.getRegister() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        return "register";
    }

    @PostMapping("/register")
    public String postRegister(Registration registration, Model model, HttpServletRequest request) {
        log.warn("AUTH_REQUEST: MainController.postRegister() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        Map<String, String> returnedMap = validationService.validateRegistration(registration);
        if (returnedMap.isEmpty()) {
            registrationService.registerNewUser(registration);
            returnedMap.put("message", "Success - Please check your email for verification link!");
            returnedMap.put("page", "register");
        }
        model.addAttribute("message", returnedMap.get("message"));
        return returnedMap.get("page");
    }

    @GetMapping("/reset")
    public String displayReset(HttpServletRequest request,
                               @RequestParam(name = "code", required = false) String code,
                               @RequestParam(name = "reVerify", defaultValue = "false", required = false) boolean reVerify,
                               Model model) {
        log.warn("AUTH_REQUEST: MainController.displayReset() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        Map<String, Object> map = passwordResetService.displayProcessor(request, code, reVerify);
        if (map.containsKey("invalidCode")) {
            return "redirect:/login";
        }
        model.addAllAttributes(map);
        return "reset";
    }

    @PostMapping("/reset")
    public String postReset(HttpServletRequest request, HttpServletResponse response, Authentication authentication,
                            Model model, Registration registration, String code) {
        log.warn("AUTH_REQUEST: MainController.postReset() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        Map<String, Object> map = passwordResetService.postProcessor(request, response, authentication, registration, code);
        model.addAllAttributes(map);
        return "reset";
    }

    @GetMapping("/verify")
    public String displayVerify(HttpServletRequest request, @RequestParam(name = "code", required = false) String code, Model model) {
        log.warn("AUTH_REQUEST: MainController.getVerify() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        log.warn("AUTH_REQUEST: MainController.getVerify() - Request parameter: code={}", code);
        // need a registration record for processor logic
        Registration registration = new Registration(null,null,null,null, false);
        if (code == null) { // no code... what are they doing here?
            return "index";
        }
        else if (verificationService.verificationCodeProcessor(code, registration)) { // happy path
            model.addAttribute("isSuccess", true);
        }
        else { // failing validation
            model.addAttribute("isSuccess", false);
        }
        return "verify";
    }
}
