package com.marcuslull.auth.services;

import com.marcuslull.auth.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    // A custom implementation of the required UserDetailsService. Spring security will use this rather than the default

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        log.info("START: CustomUserDetailsService");
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.warn("USER: CustomUserDetailsService.loadUserByUsername({})", username);
        // Showing Spring how to get the user info from our custom implementation
        return userRepository.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
