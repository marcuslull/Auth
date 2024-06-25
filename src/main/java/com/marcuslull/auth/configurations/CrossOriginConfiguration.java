package com.marcuslull.auth.configurations;

import com.marcuslull.auth.repositories.RedirectRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Configuration
public class CrossOriginConfiguration {
    // Whitelisted origins should be limited to client redirect URLs only.
    // Retrieve existing from the DB and modify the whitelist through setter at runtime.

    private final RedirectRepository redirectRepository;

    private final Set<String> corsUrlWhitelist = new HashSet<>(); // CACHE - Should be cleared whenever redirects are updated
    private final Set<String> threadSafeCorsWhitelist = Collections.synchronizedSet(corsUrlWhitelist);
    public void clearCorsWhitelist() {
        threadSafeCorsWhitelist.clear();
    }

    public CrossOriginConfiguration(RedirectRepository redirectRepository) {
        this.redirectRepository = redirectRepository;
        log.info("AUTH_START: CrossOriginConfiguration");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        return new UrlBasedCorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                // The CORS config needs to be updated during runtime as clients add/remove redirect URLs

                if (threadSafeCorsWhitelist.isEmpty()) {
                    // retrieve the list of URLs from client redirect URLS
                    updateWhitelistFromDatabase();
                }

                String requestOrigin = request.getHeader(HttpHeaders.ORIGIN);
                List<String> allowedHttpMethods = List.of("GET", "OPTION", "POST");
                if (!allowedHttpMethods.contains(request.getMethod()) || !threadSafeCorsWhitelist.contains(requestOrigin)) {
                    // just a check for logging purposes
                    log.warn("AUTH_CORS: CrossOriginConfiguration.corsConfigurationSource() - Invalid CORS request: {} on {}", request.getMethod(), requestOrigin);
                }

                // return a valid configuration
                CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
                configuration.setAllowedOrigins(threadSafeCorsWhitelist.stream().toList());
                configuration.setAllowedMethods(allowedHttpMethods);
                return configuration;
            }
        };
    }

    private void updateWhitelistFromDatabase() {
        // connect and retrieve client redirects data from the database
        redirectRepository.findAll().forEach(redirect -> threadSafeCorsWhitelist.add(redirect.getUrl()));
        log.warn("AUTH_CORS: CrossOriginConfiguration.updateWhitelistFromDatabase() - Updated whitelist: {}", threadSafeCorsWhitelist);
    }
}
