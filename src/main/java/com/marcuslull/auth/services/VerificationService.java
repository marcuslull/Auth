package com.marcuslull.auth.services;

import com.marcuslull.auth.models.User;
import com.marcuslull.auth.models.Verification;
import com.marcuslull.auth.repositories.VerificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
public class VerificationService {
    private final VerificationRepository verificationRepository;
    private final EmailService emailService;

    public VerificationService(VerificationRepository verificationRepository, EmailService emailService) {
        this.emailService = emailService;
        log.info("START: VerificationService");
        this.verificationRepository = verificationRepository;
    }

    public void verify(User user) {
        // Java does not like multiline patterns...
        String EMAIL_VERIFICATION_PATTERN ="^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
        if (Pattern.matches(EMAIL_VERIFICATION_PATTERN, user.getUsername().trim())) {
            String code = UUID.randomUUID().toString();
            Verification savedVerification = verificationRepository.save(new Verification(code, user));
            log.warn("REGISTRATION: VerificationService.verify(user: {}) - new verification code created: {}", user.getUsername(), savedVerification.getCode());
            sendVerificationEmail(user.getUsername(), savedVerification.getCode());
        } else {
            log.warn("REGISTRATION: VerificationService.verify(user: {}) - Invalid email", user.getUsername());
        }
    }

    private void sendVerificationEmail(String email, String code) {
        log.warn("REGISTRATION: VerificationService.sendVerificationEmail(email: {})", email);
        String LINK = "http://localhost:8080/verify?code=" + code;
        emailService.sendEmailVerification(email, LINK);
    }
}
