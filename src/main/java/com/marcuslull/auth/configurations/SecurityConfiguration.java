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
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    @Order(1)
    public SecurityFilterChain AuthorizationServerFilterChain(HttpSecurity http) throws Exception {
        log.info("AUTH_START: SecurityConfiguration.AuthorizationServerFilterChain()");

        // Apply base OAuth defaults for Spring Authorization server
        // This matches on all the default endpoints authorization server uses, defines the scope of this filter chain
        // to those endpoints and also disables csrf for them. Sets all other endpoints to .authenticated()
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http
                // adds the above default configurer to the configuration
                .getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                // enables OpenID Connect - Provides for the endpoints required to support OIDC
                .oidc(Customizer.withDefaults());

        http
                // any exceptions such as access denied should be redirected to /login
                .exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain baseFilter(HttpSecurity http) throws Exception {
        log.info("AUTH_START: SecurityConfiguration.baseFilter()");

        http
                // access configuration - must be ordered by specificity
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/images/**", "/favicon.ico", "/register", "/reset", "/verify", "/").permitAll()
                        .anyRequest().authenticated())

                // custom login page handling
                .formLogin(form -> form.loginPage("/login").permitAll()
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .failureUrl("/login?error")) // pass the error back to /login as a param

                // custom logout page handling
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessUrl("/success").permitAll()
                        .deleteCookies("JSESSIONID"));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        log.info("AUTH_START: SecurityConfiguration.authenticationManager()");
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(); // using the standard username/password authentication
        authenticationProvider.setUserDetailsService(userDetailsService); // using CustomUserDetailsService for the user details configuration
        authenticationProvider.setPasswordEncoder(passwordEncoder); // using argon2 password encoder below
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(true); // clearing password from mem
        return providerManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("AUTH_START: SecurityConfiguration.passwordEncoder");
        // 16B saltLength, 32B hashLength, 4 parallelism (CPU cores), 32B memory (left bit-shift by 15 places), 20 iterations
//        return new Argon2PasswordEncoder(16, 32, 4, 1 << 15, 20);
        return new Argon2PasswordEncoder(0, 32, 1, 1 << 4, 1); // testing only
    }
}
