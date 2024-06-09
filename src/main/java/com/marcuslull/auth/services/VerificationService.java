package com.marcuslull.auth.services;

import com.marcuslull.auth.models.User;
import com.marcuslull.auth.models.Verification;
import com.marcuslull.auth.models.records.Registration;
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

    public VerificationService(UserRepository userRepository, VerificationRepository verificationRepository,
                               EmailService emailService, PasswordEncoder passwordEncoder,
                               ValidationService validationService) {
        log.info("AUTH_START: VerificationService");
        this.validationService = validationService;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.verificationRepository = verificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public boolean verificationCodeProcessor(String code, Registration registration) {
        // this orchestrates four branches for verification codes - new code, password reset code, account registration code, handle expired codes
        if (code.isEmpty()) { // this branch is for new/lost codes
            User user = verificationUserGetterByUsername(registration.email());
            if (user != null) {
                if (user.isEnabled()) {
                    log.warn("AUTH_VERIFICATION: VerificationService.verificationCodeProcessor(code: {}) - User is already verified - dropping the call", code);
                    return false;
                } else {
                    log.warn("AUTH_VERIFICATION: VerificationService.verificationCodeProcessor(code: {}) - Email verified, user: {} sending new verification code, removing obsolete verification records", code, user.getUsername());
                    verificationRepository.deleteAllById(user); // remove all previous codes for the user
                    verificationCodeGenerator(user, false);
                    return true;
                }
            }
        }
        Verification verification = verificationEntryGetter(code);
        if (verification != null) {
            User user = verificationUserGetterById(verification.getUser().getId());
            if (validationService.codeIsNotExpired(verification.getCreated())) {
                if (user != null) {
                    if (registration.isReset()) { // this branch is for password resets
                        user.setPassword(passwordEncoder.encode(registration.password()));
                        userRepository.save(user);
                        verificationRepository.delete(verification);
                        log.warn("AUTH_VERIFICATION: VerificationService.verificationCodeProcessor(code: {}) - Email verified, user: {} password updated, removing obsolete verification record", code, user.getUsername());
                    }
                    else { // this branch is for new registrations
                        user.setEnabled(true);
                        userRepository.save(user);
                        verificationRepository.delete(verification);
                        log.warn("AUTH_VERIFICATION: VerificationService.verificationCodeProcessor(code: {}) - Email verified, user: {} account now enabled, removing obsolete verification record", code, user.getUsername());
                    }
                    return true;
                }
                else {
                    log.warn("AUTH_VERIFICATION: VerificationService.verificationCodeProcessor(code: {}) - User not found - dropping the call", code);
                    return false;
                }
            }
            else { // this branch is for expired verification codes
                log.warn("AUTH_VERIFICATION: VerificationService.verificationCodeProcessor(code: {}) - Verification is expired, user: {} removing obsolete verification and generating a new one", code, user.getUsername());
                verificationCodeGenerator(user, false);
                verificationRepository.delete(verification);
                return true;
            }
        }
        log.warn("AUTH_VERIFICATION: VerificationService.verificationCodeProcessor(code: {}) - User or verification not found - dropping the call", code);
        return false;
    }

    public void verificationCodeGenerator(User user, boolean isReset) {
        if (validationService.emailIsWellFormed(user)) {
            // code, timestamp and user id make up a verification. A short-lived email verification scheme
            String code = UUID.randomUUID().toString();
            Instant creationTime = Instant.now();
            Verification savedVerification = verificationRepository.save(new Verification(code, creationTime, user));
            log.warn("AUTH_VERIFICATION: VerificationService.verificationCodeGenerator(user: {}) - new code created: {} - isReset: {}", user.getUsername(), savedVerification.getCode(), isReset);
            emailService.sendEmail(user.getUsername(), savedVerification.getCode(), isReset);
        } else { // this user has not registered their account yet - just drop the call
            log.warn("AUTH_VERIFICATION: VerificationService.verificationCodeGenerator(user: {}) - Invalid email", user.getUsername());
        }
    }

    public Verification verificationEntryGetter(String code) {
        // clean up the code, sometimes the model likes to add commas to it if the user had to make several attempts at getting a well-formed password
        String cleanCode = code.split(",")[0];
        Optional<Verification> optionalVerification = verificationRepository.findByCode(cleanCode);
        return optionalVerification.orElse(null);
    }

    private User verificationUserGetterByUsername(String username) {
        Optional<User> optionalUser = userRepository.getUserByUsername(username);
        return optionalUser.orElse(null);
    }

    private User verificationUserGetterById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }
}
