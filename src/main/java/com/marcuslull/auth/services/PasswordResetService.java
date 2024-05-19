package com.marcuslull.auth.services;

import com.marcuslull.auth.models.Registration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PasswordResetService {

    private final VerificationService verificationService;
    private final RegistrationService registrationService;
    private final ValidationService validationService;
    private final LogoutService logoutService;

    public PasswordResetService(VerificationService verificationService, RegistrationService registrationService, ValidationService validationService, LogoutService logoutService) {
        this.verificationService = verificationService;
        this.registrationService = registrationService;
        this.validationService = validationService;
        this.logoutService = logoutService;
    }

    public Map<String, Object> displayProcessor(HttpServletRequest request, String code, boolean reVerify) {

        Map<String, Object> map = new HashMap<>();

        if (reVerify) { // this will be a lost code and the user will need a new one
            log.warn("AUTH_REQUEST: PasswordResetService.displayProcessor() - ReVerification {} {}", request.getRemoteAddr(), request.getRemotePort());
            map.put("isVerify", true);
            map.put("isGet", true); // the view needs to know where we are at in the process
            map.put("message", "");
        }

        // This begins the password reset flow (GET /reset > POST /reset(email) > GET /reset(resetCode) > POST /reset(resetCode, new credentials))
        else if (code == null) { // first GET - resetCode should not be present
            log.warn("AUTH_REQUEST: PasswordResetService.displayProcessor() - First time through {} {}", request.getRemoteAddr(), request.getRemotePort());
            map.put("isVerify", false);
            map.put("isGet", true); // the view needs to know where we are at in the process
            map.put("message", "");
        }

        else { // second GET, user clicked link in reset email, we need to pass the reset code to the POST form
            log.warn("AUTH_REQUEST: PasswordResetService.displayProcessor() - Second time through {} {}", request.getRemoteAddr(), request.getRemotePort());
            if (verificationService.verificationEntryGetter(code) == null) {
                log.warn("AUTH_REQUEST: PasswordResetService.displayProcessor() - Used reset code, forwarding to /login");
                map.put("invalidCode", true);
            }
            map.put("isVerify", false);
            map.put("isGet", false);
            map.put("code", code);
        }

        return map;
    }

    public String postProcessor(HttpServletRequest request, HttpServletResponse response, Authentication authentication,
                                Model model, Registration registration, String code) {
        Map<String, String> returnMap;
        if (!registration.isReset()) { // this is for lost/new account verification links
            log.warn("AUTH_REQUEST: MainController.postReset() - Attempting to re-verify on user: {}", registration.email());
            verificationService.verificationCodeProcessor("", registration);
            model.addAttribute("isVerify", false);
            model.addAttribute("isGet", true);
            model.addAttribute("message", "Please check your email for a new verification link.");
            return "reset";
        }

        else if (code == null) { // first POST, email has been submitted, hand off to service layer for email validation and reset link generation
            log.warn("AUTH_REQUEST: MainController.postReset() - Attempting a password reset on user: {}", registration.email());
            log.warn("AUTH_REQUEST: MainController.postReset() - First time through {} {}", request.getRemoteAddr(), request.getRemotePort());
            returnMap = registrationService.registerNewPassword(registration); // service layer can respond with email validity messages
            model.addAttribute("isVerify", false);
            model.addAttribute("isGet", true);
            model.addAttribute("message", returnMap.get("message"));
            return "reset";
        }

        else { // second POST, user has entered new credentials. Need hand-off to service layer for credential validation and the update
            log.warn("AUTH_REQUEST: MainController.postReset() - Second time through {} {}", request.getRemoteAddr(), request.getRemotePort());
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
}
