package com.marcuslull.auth.configurations;

import com.marcuslull.auth.repositories.RedirectRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Getter
@Setter
@Configuration
public class CrossOriginConfiguration {
    // Whitelisted origins should be limited to client redirect URLs only.
    // Retrieve existing from the DB and modify the whitelist through setter at runtime.

    private final RedirectRepository redirectRepository;
    private List<String> corsUrlWhitelist; // CACHE: update this with new Client redirects as they are registered.

    public CrossOriginConfiguration(RedirectRepository redirectRepository) {
        this.redirectRepository = redirectRepository;

    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        return new UrlBasedCorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                String requestOrigin = request.getHeader(HttpHeaders.ORIGIN);
                if (corsUrlWhitelist == null) {
                    updateWhitelistFromDatabase();
                }
                if (corsUrlWhitelist != null && corsUrlWhitelist.contains(requestOrigin)) {
                    CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
                    configuration.setAllowedMethods(List.of("GET", "POST")); // allowed HTTP methods
                    return configuration;
                }
                return null;
            }
        };
    }

    private void updateWhitelistFromDatabase() {
        // Logic to connect and retrieve whitelist data from the database
        redirectRepository.findAll().forEach(redirect -> corsUrlWhitelist.add(redirect.getUrl()));
    }
}
