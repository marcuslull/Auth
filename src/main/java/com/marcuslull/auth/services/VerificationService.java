package com.marcuslull.auth.services;

import com.marcuslull.auth.models.User;
import com.marcuslull.auth.models.Verification;
import com.marcuslull.auth.repositories.UserRepository;
import com.marcuslull.auth.repositories.VerificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
public class VerificationService {
    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final EmailService emailService;

    public VerificationService(UserRepository userRepository, VerificationRepository verificationRepository, EmailService emailService, Clock clock) {
        log.info("START: VerificationService");
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.verificationRepository = verificationRepository;
    }

    public void frontSideVerify(User user) {
        // Java does not like multiline patterns...
        String EMAIL_VERIFICATION_PATTERN ="^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

        if (Pattern.matches(EMAIL_VERIFICATION_PATTERN, user.getUsername().trim())) {
            // code, timestamp and user id make up a verification. A short-lived email verification scheme
            String code = UUID.randomUUID().toString();
            Instant creationTime = Instant.now();
            Verification savedVerification = verificationRepository.save(new Verification(code, user, creationTime));
            log.warn("REGISTRATION: VerificationService.frontSideVerify(user: {}) - new verification code created: {}", user.getUsername(), savedVerification.getCode());
            sendVerificationEmail(user.getUsername(), savedVerification.getCode());
        } else { // this user has not registered their account yet
            // TODO: Should the user be notified? If it got to this point the email structure has been validated 2X already so this should never really happen
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

            if (!isExpired(creationTime)) {
                Optional<User> optionalUser = userRepository.getUserById(verification.getId().getId()); // TODO: Need to de-couple the relation in JPA
                if (optionalUser.isPresent()) { // HAPPY PATH - the user, email, and code are verified, enable the account
                    User user = optionalUser.get();
                    user.setEnabled(true);
                    userRepository.save(user);
                    verificationRepository.delete(verification);
                    log.warn("REGISTRATION: VerificationService.backSideVerify(user: {}) - Email verified, user: {} account now enabled, removing obsolete verification record", code, user.getUsername());
                    return true;
                }
            }
            log.warn("REGISTRATION: VerificationService.backSideVerify(code: {}) - Verification is expired, removing obsolete verification record", code);
            verificationRepository.delete(verification);
            return false;
        }
        log.warn("REGISTRATION: VerificationService.backSideVerify(code: {}) - Verification or User not found", code);
        return false;
    }

    private boolean isExpired(Instant created){
        Instant now = Instant.now();
        Duration duration = Duration.between(created, now);
        return duration.getSeconds() > 3600; // 1 hour expiration
}

    private void sendVerificationEmail(String email, String code) {
        log.warn("REGISTRATION: VerificationService.sendVerificationEmail(email: {})", email);
        String LINK = "http://localhost:8080/verify?code=" + code;
        emailService.sendEmailVerification(email, LINK);
    }
}
