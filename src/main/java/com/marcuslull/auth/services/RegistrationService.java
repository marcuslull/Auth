package com.marcuslull.auth.services;

import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.models.User;
import com.marcuslull.auth.models.Verification;
import com.marcuslull.auth.repositories.UserRepository;
import com.marcuslull.auth.repositories.VerificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class RegistrationService {
    private final VerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    public final ValidationService validationService;

    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationService verificationService,
                               VerificationRepository verificationRepository, ValidationService validationService) {
        this.verificationService = verificationService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
        log.info("START: RegistrationService");
        this.verificationRepository = verificationRepository;
    }

    public void registerNewVerificationCode(String expiredCode) {
        Optional<Verification> optionalVerification = verificationRepository.findByCode(expiredCode);
        if (optionalVerification.isPresent()) {
            Verification verification = optionalVerification.get();
            Optional<User> optionalUser = userRepository.getUserById(verification.getId().getId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                log.warn("REGISTRATION: RegistrationService.resendVerificationCode({}) - Found code and user, handing off to verification service and removing obsolete verification", expiredCode);
                verificationService.frontSideVerify(user, false);
                verificationRepository.delete(verification);
            } else { log.warn("REGISTRATION: RegistrationService.resendVerificationCode({}) - User not found, dropping the call", expiredCode); }
        } else { log.warn("REGISTRATION: RegistrationService.resendVerificationCode({}) - Verification not found, dropping the call", expiredCode); }
    }

    public void registerNewUser(Registration registration) {
        User user = new User();
        user.setUsername(registration.email());
        user.setPassword(passwordEncoder.encode(registration.password()));
        user.setEnabled(false);
        user.setGrantedAuthority(Collections.singletonList(new SimpleGrantedAuthority("USER")));
        User savedUser = userRepository.save(user);
        log.warn("REGISTRATION: RegistrationService.registerNewUser(email: {}, password: [PROTECTED]) - new user registered", savedUser.getUsername());
        verificationService.frontSideVerify(savedUser, false);
    }

    public Map<String, String> registerNewPassword(Registration registration) {
        Map<String, String> returnMap = new HashMap<>();
        Optional<User> optionalUser = userRepository.getUserByUsername(registration.email());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            verificationService.frontSideVerify(user, true);
            returnMap.put("message", "A password reset will be emailed to you soon");
            return returnMap;
        }
        log.warn("REGISTRATION: RegistrationService.resetPassword({}) - User was not found, dropping the call", registration.email());
        returnMap.put("message", "A password reset will be emailed to you soon");
        return returnMap;
    }
}
