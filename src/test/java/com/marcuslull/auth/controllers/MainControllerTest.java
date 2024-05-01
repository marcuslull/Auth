package com.marcuslull.auth.controllers;

import com.marcuslull.auth.models.Registration;
import com.marcuslull.auth.services.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {MainController.class})
@AutoConfigureMockMvc
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterService registerService;


    @BeforeEach
    public void setup() {
        // I guess tests have a hard time finding Thymeleaf templates. This is a helper, we add to the mock mvc
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
    @WithAnonymousUser
    public void getIndexAsAnon() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("isAnon", true));
    }

    @Test
    public void getRegisterTest() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    public void postRegisterTest() throws Exception {
        // arrange
        Map<String, String> expectedReturnMap = new HashMap<>();
        expectedReturnMap.put("message", "Success!");
        expectedReturnMap.put("page", "redirect:/login");
        when(registerService.registrationProcess(any(Registration.class))).thenReturn(expectedReturnMap);

        // act
        mockMvc.perform(post("/register"));

        // assert
        verify(registerService, times(1)).registrationProcess(any(Registration.class));
    }

    @Test
    public void getResetTest() throws Exception {
        mockMvc.perform(get("/reset"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset"));
    }
}