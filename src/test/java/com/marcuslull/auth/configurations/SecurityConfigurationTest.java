package com.marcuslull.auth.configurations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {SecurityConfiguration.class, TestConfiguration.class})
@AutoConfigureMockMvc
class SecurityConfigurationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setup() {
        UserDetails user = User.builder()
                .passwordEncoder(passwordEncoder::encode)
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        when(userDetailsService.loadUserByUsername("user")).thenReturn(user);
    }

    @Test
    @WithMockUser
    void shouldAuthorizeAuthenticatedUser() throws Exception {
        mvc.perform(get("/login").with(user(userDetailsService.loadUserByUsername("user"))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRedirectUnauthenticatedUserToLoginForm() throws Exception {
        mvc.perform(get("/invalid"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void shouldAuthenticateUserWithValidCredentials() throws Exception {
        mvc.perform(formLogin().user("user").password("password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void shouldNotAuthenticateUserWithInvalidCredentials() throws Exception {
        mvc.perform(formLogin().user("invalid").password("invalid"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }
}