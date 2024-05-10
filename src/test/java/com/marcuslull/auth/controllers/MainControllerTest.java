package com.marcuslull.auth.controllers;


import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.services.RegistrationService;
import com.marcuslull.auth.services.ValidationService;
import com.marcuslull.auth.services.VerificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MainController.class)
@AutoConfigureMockMvc(addFilters = false) // bypasses the security filters
class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RegistrationService registrationService;
    @MockBean
    private VerificationService verificationService;
    @MockBean
    private ValidationService validationService;

    @Test
    @WithAnonymousUser
    void getIndex() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @WithAnonymousUser
    void getRegisterTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/register")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    @WithAnonymousUser
    void postRegisterTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("email", "email@email.com")
                        .param("password","password")
                        .param("confirmPassword", "password")
                        .param("isReset", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("message","Success - Please check your email for verification link!"));

        verify(validationService, atLeastOnce()).validateRegistration(any(Registration.class));
        verify(registrationService, atLeastOnce()).registerNewUser(any(Registration.class));
    }

    @Test
    @WithAnonymousUser
    void getResetWithNoCodeTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/reset")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("reset"))
                .andExpect(model().attribute("isGet",true))
                .andExpect(model().attribute("message",""));
    }

    @Test
    @WithAnonymousUser
    void getResetWithCodeTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/reset")
                        .with(csrf())
                        .queryParam("code", "randomUUID"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset"))
                .andExpect(model().attribute("isGet",false))
                .andExpect(model().attribute("code","randomUUID"));
    }

    @Test
    @WithAnonymousUser
    void getResetWithReVerifyTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/reset")
                        .with(csrf())
                        .queryParam("reVerify", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset"))
                .andExpect(model().attribute("isVerify", true))
                .andExpect(model().attribute("isGet",true))
                .andExpect(model().attribute("message",""));
    }

    @Test
    @WithAnonymousUser
    void postResetWithNoCodeTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/reset")
                        .with(csrf())
                        .param("password", "email@email.com")
                        .param("isReset", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset"))
                .andExpect(model().attribute("isGet",true));

        verify(registrationService, atLeastOnce()).registerNewPassword(any(Registration.class));
    }

    @Test
    @WithAnonymousUser
    void postResetWithCodeTest() throws Exception {
        when(verificationService.verificationCodeProcessor(anyString(), any(Registration.class))).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/reset")
                        .with(csrf())
                        .param("password", "password")
                        .param("confirmPassword", "password")
                        .param("isReset", "true")
                        .param("code", "randomUUID"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset"))
                .andExpect(model().attribute("message","Success - please login."));

        verify(validationService, atLeastOnce()).validatePasswordReset(any(Registration.class));
        verify(verificationService, atLeastOnce()).verificationCodeProcessor(anyString(), any(Registration.class));
    }

    @Test
    @WithAnonymousUser
    void postResetWithReVerifyTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/reset")
                        .with(csrf())
                        .param("password", "email@email.com")
                        .param("isReset", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset"))
                .andExpect(model().attribute("isVerify",false))
                .andExpect(model().attribute("isGet",true))
                .andExpect(model().attribute("message","Please check your email for a new verification link."));

        verify(verificationService, atLeastOnce()).verificationCodeProcessor(anyString(), any(Registration.class));
    }

    @Test
    @WithAnonymousUser
    void getVerifyWithRequestParamsTest() throws Exception {
        when(verificationService.verificationCodeProcessor(anyString(), any(Registration.class))).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get("/verify")
                        .queryParam("code", "randomUUID"))
                .andExpect(status().isOk())
                .andExpect(view().name("verify"))
                .andExpect(model().attribute("isSuccess",true));

        verify(verificationService, atLeastOnce()).verificationCodeProcessor(anyString(), any(Registration.class));
    }

    @Test
    @WithAnonymousUser
    void getVerifyWithNoRequestParamsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/verify"))
                .andExpect(view().name("index"));
    }
}