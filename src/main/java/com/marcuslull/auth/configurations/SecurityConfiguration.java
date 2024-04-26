package com.marcuslull.auth.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    @Order(1)
    public SecurityFilterChain baseFilter(HttpSecurity http) throws Exception {
        // base security config for Spring Security
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()) // all requests to the service must be authenticated
                .formLogin(Customizer.withDefaults()); // using the default spring security login page
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        // Creating a required AuthenticationManger, ProviderManager, and AuthenticationProvider for Spring Security
        // UserDetailsService will be our CustomUserDetailsService. PasswordEncoder will be the Argon2 Bean declared below
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(); // using the standard username/password authentication
        authenticationProvider.setUserDetailsService(userDetailsService); // using CustomUserDetailsService for the user details configuration
        authenticationProvider.setPasswordEncoder(passwordEncoder); // using argon2 password encoder below

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false); // retaining the password for the session duration
        return providerManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Argon2 encoder is a current best practice for password hashing
        // 16B saltLength, 32B hashLength, 4 parallelism (CPU cores), 32B memory (left bit-shift by 15 places), 20 iterations
        return new Argon2PasswordEncoder(16, 32, 4, 1 << 15, 20);
    }
}
