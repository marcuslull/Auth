package com.marcuslull.auth.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    public static final String FROM = "mjlappsdemo@gmail.com";
    public static final String RESET_SUBJECT = "Password reset from MJLApps";
    public static final String LINK_BASE = "http://localhost:8080";
//    public static final String LINK_BASE = "https://auth.marcuslull.com";
    public static final String RESET_LINK = LINK_BASE + "/reset?code=";
    public static final String RESET_BASE_TEXT = "Please click the following link to reset your password: ";
    public static final String VERIFY_SUBJECT = "Email Verification from MJLApps";
    public static final String VERIFY_LINK = LINK_BASE + "/verify?code=";
    public static final String VERIFY_BASE_TEXT = "Please click the following link to verify your email account: ";

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        log.info("START: EmailService");
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String code, boolean isReset) {
        SimpleMailMessage simpleMailMessage = getSimpleMailMessageProcessor(to, code, isReset);
        try {
            javaMailSender.send(simpleMailMessage);
            log.warn("EMAIL: EmailService.sendEmail(email: {} link: {}) - Success, message sent", to, code);
        } catch (Exception e) {
            log.warn("EMAIL: EmailService.sendEmail(email: {} link: {}) - Failure, authentication or network error", to, code);
            throw new RuntimeException(e);
        }
    }

    private SimpleMailMessage getSimpleMailMessageProcessor(String to, String code, boolean isReset) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        if (isReset) {
            log.warn("EMAIL: EmailService.getSimpleMailMessageProcessor(email: {} code: {}) - Building password reset email", to, code);
            simpleMailMessage.setFrom(FROM);
            simpleMailMessage.setSubject(RESET_SUBJECT);
            simpleMailMessage.setText(RESET_BASE_TEXT + RESET_LINK + code);
        } else {
            log.warn("EMAIL: EmailService.getSimpleMailMessageProcessor(email: {} code: {}) - Building account register email", to, code);
            simpleMailMessage.setFrom(FROM);
            simpleMailMessage.setSubject(VERIFY_SUBJECT);
            simpleMailMessage.setText(VERIFY_BASE_TEXT + VERIFY_LINK + code);
        }
        return simpleMailMessage;
    }
}
