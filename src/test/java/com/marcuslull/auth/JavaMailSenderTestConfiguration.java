package com.marcuslull.auth;

import jakarta.mail.internet.MimeMessage;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@TestConfiguration
public class JavaMailSenderTestConfiguration {

    private JavaMailSender javaMailSender;

    private MimeMessage mimeMessage;

    @Bean
    JavaMailSender mockMailSender() {
        mimeMessage = Mockito.mock(MimeMessage.class);
        javaMailSender = Mockito.mock(JavaMailSender.class);
        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        return javaMailSender;
    }
}
