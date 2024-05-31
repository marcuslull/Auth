package com.marcuslull.auth.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false) // bypasses the security filters
class AuthenticationControllerTestType {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithAnonymousUser
    void displayLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithAnonymousUser
    void postLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithAnonymousUser
    void displayLogout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/logout")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("logout"));
    }

    @Test
    @WithAnonymousUser
    void postLogout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("logout"));
    }

    @Test
    @WithAnonymousUser
    void displaySuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/success")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("success"));
    }

}