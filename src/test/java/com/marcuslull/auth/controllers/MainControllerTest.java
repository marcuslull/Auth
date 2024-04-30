package com.marcuslull.auth.controllers;

import com.marcuslull.auth.configurations.SecurityConfiguration;
import com.marcuslull.auth.configurations.TestConfiguration;
import com.marcuslull.auth.services.RegisterService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(classes = {MainController.class, SecurityConfiguration.class, TestConfiguration.class})
@AutoConfigureMockMvc
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private RegisterService registerService;


    @BeforeEach
    public void setup() {
        UserDetails user = User.builder()
                .passwordEncoder(passwordEncoder::encode)
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        when(userDetailsService.loadUserByUsername("user")).thenReturn(user);

        // I guess tests have a hard time finding Thymeleaf templates. This is a helper
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(new MainController(registerService))
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void getIndexTest() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void getRegisterTest() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

//    @Test
//    public void postRegisterTest() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.post("/register")
//                        .contentType(MediaType.APPLICATION_FORM_URLENCODED) // Set content type
//                        .param("email", "test@test.com")
//                        .param("password", "StrongPassword1!")
//                        .param("confirmPassword", "StrongPassword1!"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("index"));
//    }
}