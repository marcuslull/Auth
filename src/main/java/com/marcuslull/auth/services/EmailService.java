package com.marcuslull.auth.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        log.info("START: EmailService");
        this.javaMailSender = javaMailSender;
    }

    public void sendEmailVerification(String to, String link) {
        final String FROM = "mjlappsdemo@gmail.com";
        final String SUBJECT = "Email Verification from MJLApps";
        final String BASE_TEXT = "Please click the following link to verify your email account. ";

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(to);
        simpleMailMessage.setFrom(FROM);
        simpleMailMessage.setSubject(SUBJECT);
        simpleMailMessage.setText(BASE_TEXT + link);

        try {
            javaMailSender.send(simpleMailMessage);
            log.warn("REGISTRATION: EmailService.sendEmailVerification(email: {} link: {}) - Success, message sent", to, link);
        } catch (Exception e) {
            log.warn("REGISTRATION: EmailService.sendEmailVerification(email: {} link: {}) - Failure, authentication or network error", to, link);
            throw new RuntimeException(e);
        }
    }
}
