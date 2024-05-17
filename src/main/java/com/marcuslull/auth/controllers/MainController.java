package com.marcuslull.auth.controllers;

import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.services.LogoutService;
import com.marcuslull.auth.services.RegistrationService;
import com.marcuslull.auth.services.ValidationService;
import com.marcuslull.auth.services.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
    private final LogoutService logoutService;

    public MainController(RegistrationService registrationService, VerificationService verificationService, ValidationService validationService, LogoutService logoutService) {
        this.registrationService = registrationService;
        this.verificationService = verificationService;
        this.validationService = validationService;
        this.logoutService = logoutService;
        log.info("START: MainController");
    }

    @ModelAttribute("isAnon")
    public void isAnon(Principal principal, Model model) {
        model.addAttribute("isAnon", principal == null);
    }

    @GetMapping("/")
    public String displayIndex(HttpServletRequest request) {
        log.warn("REQUEST: MainController.getIndex() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        return "index";
    }

    @GetMapping("/register")
    public String displayRegister(HttpServletRequest request) {
        log.warn("REQUEST: MainController.getRegister() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        return "register";
    }

    @PostMapping("/register")
    public String postRegister(Registration registration, Model model, HttpServletRequest request) {
        log.warn("REQUEST: MainController.postRegister() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        Map<String, String> returnedMap = validationService.validateRegistration(registration);
        if (returnedMap.isEmpty()) {
            registrationService.registerNewUser(registration);
            returnedMap.put("message", "Success - Please check your email for verification link!");
            returnedMap.put("page", "register");
        }
        model.addAttribute("message", returnedMap.get("message"));
        return returnedMap.get("page");
    }

    // TODO: The logic here needs to in the service layer to simplify the controller method
    @GetMapping("/reset")
    public String displayReset(HttpServletRequest request, @RequestParam(name = "code", required = false) String code,
                               @RequestParam(name = "reVerify", defaultValue = "false", required = false) boolean reVerify,
                               Model model) {
        log.warn("REQUEST: MainController.getReset() - {} {}", request.getRemoteAddr(), request.getRemotePort());

        if (reVerify) {
            log.warn("REQUEST: MainController.getReset() - ReVerification {} {}", request.getRemoteAddr(), request.getRemotePort());
            model.addAttribute("isVerify", true);
            model.addAttribute("isGet", true); // the view needs to know where we are at in the process
            model.addAttribute("message", "");
        }

        // This begins the password reset flow (GET /reset > POST /reset(email) > GET /reset(resetCode) > POST /reset(resetCode, new credentials))
        else if (code == null) { // first GET - resetCode should not be present
            log.warn("REQUEST: MainController.getReset() - First time through {} {}", request.getRemoteAddr(), request.getRemotePort());
            model.addAttribute("isVerify", false);
            model.addAttribute("isGet", true); // the view needs to know where we are at in the process
            model.addAttribute("message", "");
        }

        else { // second GET, user clicked link in reset email, we need to pass the reset code to the POST form
            log.warn("REQUEST: MainController.getReset() - Second time through {} {}", request.getRemoteAddr(), request.getRemotePort());
            model.addAttribute("isVerify", false);
            model.addAttribute("isGet", false);
            model.addAttribute("code", code);
        }

        return "reset";
    }

    // TODO: The logic here needs to in the service layer to simplify the controller method
    @PostMapping("/reset")
    public String postReset(HttpServletRequest request, HttpServletResponse response, Authentication authentication,
                            Model model, Registration registration, String code) {
        log.warn("REQUEST: MainController.postReset() - {} {}", request.getRemoteAddr(), request.getRemotePort());

        Map<String, String> returnMap;
        if (!registration.isReset()) { // this is for lost/new account verification links
            log.warn("REQUEST: MainController.postReset() - Attempting to re-verify on user: {}", registration.email());
            verificationService.verificationCodeProcessor("", registration);
            model.addAttribute("isVerify", false);
            model.addAttribute("isGet", true);
            model.addAttribute("message", "Please check your email for a new verification link.");
            return "reset";
        }

        else if (code == null) { // first POST, email has been submitted, hand off to service layer for email validation and reset link generation
            log.warn("REQUEST: MainController.postReset() - Attempting a password reset on user: {}", registration.email());
            log.warn("REQUEST: MainController.postReset() - First time through {} {}", request.getRemoteAddr(), request.getRemotePort());
            returnMap = registrationService.registerNewPassword(registration); // service layer can respond with email validity messages
            model.addAttribute("isVerify", false);
            model.addAttribute("isGet", true);
            model.addAttribute("message", returnMap.get("message"));
            return "reset";
        }

        else { // second POST, user has entered new credentials. Need hand-off to service layer for credential validation and the update
            log.warn("REQUEST: MainController.postReset() - Second time through {} {}", request.getRemoteAddr(), request.getRemotePort());
            returnMap = validationService.validatePasswordReset(registration);
            if (returnMap.containsKey("message")) { // any password validity issues are returned to the view here
                model.addAttribute("message", returnMap.get("message"));
                model.addAttribute("isVerify", false);
                return "reset";
            }
            if (verificationService.verificationCodeProcessor(code, registration)) {
                logoutService.logout(request, response, authentication);
                model.addAttribute("isAnon", true);
                model.addAttribute("message", "Success - please login.");
                model.addAttribute("isVerify", false);
                return "reset";
            }
            model.addAttribute("isGet", true);
        }

        return "reset";
    }

    @GetMapping("/verify")
    public String displayVerify(HttpServletRequest request, @RequestParam(name = "code", required = false) String code, Model model) {
        log.warn("REQUEST: MainController.getVerify() - {} {}", request.getRemoteAddr(), request.getRemotePort());
        log.warn("REQUEST: MainController.getVerify() - Request parameter: code={}", code);
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
