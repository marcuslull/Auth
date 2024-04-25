package com.marcuslull.auth;

import com.marcuslull.auth.models.User;
import com.marcuslull.auth.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // DEMO USERS - THERE IS NOTHING IMPORTANT HERE
        User user = new User();
        user.setUsername("user");
        user.setPassword("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW");
        user.setEnabled(true);
        user.setGrantedAuthority(List.of(new SimpleGrantedAuthority("USER")));
        userRepository.save(user);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW");
        admin.setEnabled(true);
        admin.setGrantedAuthority(List.of(new SimpleGrantedAuthority("ADMIN")));
        userRepository.save(admin);

        User returnedUser = userRepository.getUserByUsername("user").orElseThrow();
        System.out.println(returnedUser.getId());
        System.out.println(returnedUser.getUsername());
        System.out.println(returnedUser.getPassword());
        System.out.println(returnedUser.getAuthorities());
        System.out.println(returnedUser.getEnabled());

        User returnedAdmin = userRepository.getUserByUsername("admin").orElseThrow();
        System.out.println(returnedAdmin.getId());
        System.out.println(returnedAdmin.getUsername());
        System.out.println(returnedAdmin.getPassword());
        System.out.println(returnedAdmin.getAuthorities());
        System.out.println(returnedAdmin.getEnabled());
    }
}
