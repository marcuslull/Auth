package com.marcuslull.auth;

import com.marcuslull.auth.models.Client;
import com.marcuslull.auth.models.ClientAuthorization;
import com.marcuslull.auth.models.User;
import com.marcuslull.auth.models.enums.AuthType;
import com.marcuslull.auth.models.enums.GrantType;
import com.marcuslull.auth.models.enums.ScopeType;
import com.marcuslull.auth.repositories.ClientRepository;
import com.marcuslull.auth.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public DataLoader(ClientRepository clientRepository,
                      UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void run(String... args) {

        User user = new User();
        user.setUsername("marcuslull@gmail.com");
        user.setPassword(passwordEncoder.encode("3&&3YU5UxH%vm$2Zt"));
        user.setEnabled(true);
        user.setGrantedAuthority(Collections.singletonList(new SimpleGrantedAuthority("USER")));

        userRepository.save(user);

        Client client = new Client();
        client.setClientId("someId");
        client.setName("testClient");
        client.setSecret(passwordEncoder.encode("secret"));
        client.setClientSettings(ClientSettings.builder().build());
        client.setTokenSettings(TokenSettings.builder().build());
        client.addScope(ScopeType.OPENID);
        client.addScope(ScopeType.FULL);
        client.addGrant(GrantType.AUTHORIZATION_CODE);
        client.addGrant(GrantType.CLIENT_CREDENTIALS);
        client.addAuth(AuthType.CLIENT_SECRET_BASIC);
        client.addAuth(AuthType.CLIENT_SECRET_POST);
        client.addPreRedirect("http://127.0.0.1:1234/home");
        client.addPostRedirect("http://127.0.0.1:1234/home");
        client.setAuthorizations(List.of(new ClientAuthorization()));
        clientRepository.save(client);

//        clientRepository.findById(51L).ifPresent(result -> {
//                    result.getAvailableScopes().forEach(scope -> System.out.println(scope.getScope()));
//                    result.getAvailableGrants().forEach(grant -> System.out.println(grant.getGrantType()));
//                    result.getAuthMethods().forEach(auth -> System.out.println(auth.getAuthType()));
//                    result.getRedUris().forEach(red -> System.out.println(red.getUrl()));
//                    result.getPostLogRedUris().forEach(red -> System.out.println(red.getUrl()));
//                });
    }
}