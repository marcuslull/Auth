package com.marcuslull.auth.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    private final String FROM = "mjlappsdemo@gmail.com";

    private final String VERIFY_LINK = "http://localhost:8080/verify?code=";
    private final String VERIFY_SUBJECT = "Email Verification from MJLApps";
    private final String VERIFY_BASE_TEXT = "Please click the following link to verify your email account: ";

    private final String RESET_LINK = "http://localhost:8080/reset?code=";
    private final String RESET_SUBJECT = "Password reset from MJLApps";
    private final String RESET_BASE_TEXT = "Please click the following link to reset your password: ";

    public EmailService(JavaMailSender javaMailSender) {
        log.info("START: EmailService");
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String code, boolean isReset) {
        SimpleMailMessage simpleMailMessage = getSimpleMailMessage(to, code, isReset);

        try {
            javaMailSender.send(simpleMailMessage);
            log.warn("REGISTRATION: EmailService.sendEmail(email: {} link: {}) - Success, message sent", to, code);
        } catch (Exception e) {
            log.warn("REGISTRATION: EmailService.sendEmail(email: {} link: {}) - Failure, authentication or network error", to, code);
            throw new RuntimeException(e);
        }
    }

    private SimpleMailMessage getSimpleMailMessage(String to, String code, boolean isReset) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);

        if (isReset) {
            simpleMailMessage.setFrom(FROM);
            simpleMailMessage.setSubject(RESET_SUBJECT);
            simpleMailMessage.setText(RESET_BASE_TEXT + RESET_LINK + code);
        } else {
            simpleMailMessage.setFrom(FROM);
            simpleMailMessage.setSubject(VERIFY_SUBJECT);
            simpleMailMessage.setText(VERIFY_BASE_TEXT + VERIFY_LINK + code);
        }
        return simpleMailMessage;
    }
}
