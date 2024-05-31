package com.marcuslull.auth;

import com.marcuslull.auth.models.Client;
import com.marcuslull.auth.models.ClientAuthorization;
import com.marcuslull.auth.models.Redirect;
import com.marcuslull.auth.models.enums.AuthType;
import com.marcuslull.auth.models.enums.GrantType;
import com.marcuslull.auth.models.enums.ScopeType;
import com.marcuslull.auth.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {

    private final ClientRepository clientRepository;


    public DataLoader(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    @Override
    public void run(String... args) {
        Client client = new Client();
        client.setClientId(UUID.randomUUID().toString());
        client.setName("testClient");
        client.setSecret("secret");
        client.setClientSettings(ClientSettings.builder().build());
        client.setTokenSettings(TokenSettings.builder().build());
        client.addScope(ScopeType.OPENID);
        client.addScope(ScopeType.FULL);
        client.addGrant(GrantType.AUTHORIZATION_CODE);
        client.addGrant(GrantType.CLIENT_CREDENTIALS);
        client.addAuth(AuthType.CLIENT_SECRET_BASIC);
        client.addAuth(AuthType.CLIENT_SECRET_POST);
        client.setRedUris(List.of(new Redirect()));
        client.setPostLogRedUris(List.of(new Redirect()));
        client.setAuthorizations(List.of(new ClientAuthorization()));
        clientRepository.save(client);

        clientRepository.findById(51L).ifPresent(result -> {
                    result.getAvailableScopes().forEach(scope -> System.out.println(scope.getScope()));
                    result.getAvailableGrants().forEach(grant -> System.out.println(grant.getGrantType()));
                    result.getAuthMethods().forEach(auth -> System.out.println(auth.getAuthType()));
                });
    }
}