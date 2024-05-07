package com.marcuslull.auth.services;

import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.models.User;
import com.marcuslull.auth.models.Verification;
import com.marcuslull.auth.repositories.UserRepository;
import com.marcuslull.auth.repositories.VerificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class VerificationService {
    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ValidationService validationService;

    public VerificationService(UserRepository userRepository, VerificationRepository verificationRepository, EmailService emailService, PasswordEncoder passwordEncoder, ValidationService validationService) {
        this.validationService = validationService;
        log.info("START: VerificationService");
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.verificationRepository = verificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void frontSideVerify(User user, boolean isReset) {

        if (validationService.emailIsWellFormed(user)) {
            // code, timestamp and user id make up a verification. A short-lived email verification scheme
            String code = UUID.randomUUID().toString();
            Instant creationTime = Instant.now();
            Verification savedVerification = verificationRepository.save(new Verification(code, user, creationTime));
            log.warn("REGISTRATION: VerificationService.frontSideVerify(user: {}) - new code created: {} - isReset: {}", user.getUsername(), savedVerification.getCode(), isReset);
            emailService.sendEmail(user.getUsername(), savedVerification.getCode(), isReset);
        } else { // this user has not registered their account yet - just drop the call
            log.warn("REGISTRATION: VerificationService.frontSideVerify(user: {}) - Invalid email", user.getUsername());
        }
    }

    @Transactional
    public boolean backSideVerify(String code) {
        // verifies the user, the code, and the expiration are all valid and enables the account
        Optional<Verification> optionalVerification = verificationRepository.findByCode(code);
        if (optionalVerification.isPresent()) {
            Verification verification = optionalVerification.get();
            Instant creationTime = verification.getCreated();

            if (validationService.codeIsNotExpired(creationTime)) {
                Optional<User> optionalUser = userRepository.getUserById(verification.getId().getId());
                if (optionalUser.isPresent()) { // HAPPY PATH - the user, email, and code are verified, enable the account
                    User user = optionalUser.get();
                    user.setEnabled(true);
                    userRepository.save(user);
                    verificationRepository.delete(verification);
                    log.warn("REGISTRATION: VerificationService.backSideVerify(code: {}) - Email verified, user: {} account now enabled, removing obsolete verification record", code, user.getUsername());
                    return true;
                }
            }
            log.warn("REGISTRATION: VerificationService.backSideVerify(code: {}) - Verification is expired", code);
            return false;
        }
        log.warn("REGISTRATION: VerificationService.backSideVerify(code: {}) - Verification or User not found - dropping the call", code);
        return false;
    }

    @Transactional
    public boolean backSideVerify(String code, Registration registration) {
        // clean up the code, sometimes the model likes to add commas to it if the user had to make several attempts at getting a well-formed password
        String cleanCode = code.split(",")[0];
        // verifies the user, the code, and the expiration are all valid and enables the account
        Optional<Verification> optionalVerification = verificationRepository.findByCode(cleanCode);
        if (optionalVerification.isPresent()) {
            Verification verification = optionalVerification.get();
            Instant creationTime = verification.getCreated();

            if (validationService.codeIsNotExpired(creationTime)) {
                Optional<User> optionalUser = userRepository.getUserById(verification.getId().getId());
                if (optionalUser.isPresent()) { // HAPPY PATH - the user, email, and code are verified, enable the account
                    User user = optionalUser.get();
                    user.setPassword(passwordEncoder.encode(registration.password()));
                    userRepository.save(user);
                    verificationRepository.delete(verification);
                    log.warn("REGISTRATION: VerificationService.backSideVerify(code: {}) - Email verified, user: {} password updated, removing obsolete verification record", cleanCode, user.getUsername());
                    return true;
                }
            }
            log.warn("REGISTRATION: VerificationService.backSideVerify(code: {}) - Verification is expired", cleanCode);
            return false;
        }
        log.warn("REGISTRATION: VerificationService.backSideVerify(code: {}) - Verification or User not found", cleanCode);
        return false;
    }
}
