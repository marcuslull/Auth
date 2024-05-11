package com.marcuslull.auth.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
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
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    @Order(1)
    public SecurityFilterChain baseFilter(HttpSecurity http) throws Exception {
        log.info("START: SecurityConfiguration.baseFilter()");
        http
                .exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
                .authorizeHttpRequests(authorize -> authorize
                        // must be ordered by specificity
                        .requestMatchers("/images/**", "/favicon.ico", "/register", "/reset", "/verify", "/").permitAll()
                        .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        log.info("START: SecurityConfiguration.authenticationManager()");
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(); // using the standard username/password authentication
        authenticationProvider.setUserDetailsService(userDetailsService); // using CustomUserDetailsService for the user details configuration
        authenticationProvider.setPasswordEncoder(passwordEncoder); // using argon2 password encoder below
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false); // retaining the password for the session duration
        return providerManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("START: SecurityConfiguration.passwordEncoder");
        // 16B saltLength, 32B hashLength, 4 parallelism (CPU cores), 32B memory (left bit-shift by 15 places), 20 iterations
//        return new Argon2PasswordEncoder(16, 32, 4, 1 << 15, 20); // TODO: Change before PROD
        return new Argon2PasswordEncoder(0, 32, 1, 1 << 4, 1); // testing only
    }
}
