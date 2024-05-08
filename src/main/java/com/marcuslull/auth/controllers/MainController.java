package com.marcuslull.auth.controllers;

import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.services.RegistrationService;
import com.marcuslull.auth.services.ValidationService;
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

    private final RegistrationService registrationService;
    private final VerificationService verificationService;
    private final ValidationService validationService;

    public MainController(RegistrationService registrationService, VerificationService verificationService, ValidationService validationService) {
        this.registrationService = registrationService;
        this.verificationService = verificationService;
        this.validationService = validationService;
        log.info("START: MainController");
    }

    @GetMapping("/")
    public String getIndex(HttpServletRequest request, Model model, Principal principal) {
        log.info("REQUEST: MainController.getIndex() - {} {}", request.getRemoteAddr(), request.getRemotePort());
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
    public String getReset(HttpServletRequest request, @RequestParam(name = "code", required = false) String code, Model model) {
        log.info("REQUEST: MainController.getReset() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        // This begins the password reset flow (GET /reset > POST /reset(email) > GET /reset(resetCode) > POST /reset(resetCode, new credentials))
        if (code == null) { // first GET - resetCode should not be present
            log.info("REQUEST: MainController.getReset() - First time through {} {}", request.getRemoteAddr(), request.getRemotePort());
            model.addAttribute("isGet", true); // the view needs to know where we are at in the process
            model.addAttribute("message", "");
        }
        else { // second GET, user clicked link in reset email, we need to pass the reset code to the POST form
            log.info("REQUEST: MainController.getReset() - Second time through {} {}", request.getRemoteAddr(), request.getRemotePort());
            model.addAttribute("isGet", false);
            model.addAttribute("code", code);
        }
        return "reset";
    }

    @PostMapping("/reset")
    public String postReset(HttpServletRequest request, Model model, Registration registration, String code) {
        log.info("REQUEST: MainController.postReset() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        Map<String, String> returnMap;
        if (code == null) { // first POST, email has been submitted, hand off to service layer for email validation and reset link generation
            log.warn("REQUEST: MainController.postReset() - Attempting a password reset on user: {}", registration.email());
            log.info("REQUEST: MainController.postReset() - First time through {} {}", request.getRemoteAddr(), request.getRemotePort());
            returnMap = registrationService.registerNewPassword(registration); // service layer can respond with email validity messages
            model.addAttribute("isGet", true);
            model.addAttribute("message", returnMap.get("message"));
        }
        else { // second POST, user has entered new credentials. Need hand-off to service layer for credential validation and the update
            log.info("REQUEST: MainController.postReset() - Second time through {} {}", request.getRemoteAddr(), request.getRemotePort());
            returnMap = validationService.validatePasswordReset(registration);
            if (returnMap.containsKey("message")) { // any password validity issues are returned to the view here
                model.addAttribute("message", returnMap.get("message"));
                return "reset";
            }
            if (verificationService.verificationCodeProcessor(code, registration)) {
                model.addAttribute("message", "Success - please login.");
                return "reset";
            }
            model.addAttribute("isGet", true);
        }
        return "reset";
    }

    @GetMapping("/verify")
    public String getVerify(HttpServletRequest request, @RequestParam(name = "code", required = false) String code, Model model) {
        log.info("REQUEST: MainController.getVerify() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        log.info("REQUEST: MainController.getVerify() - Request parameter: code={}", code);
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
