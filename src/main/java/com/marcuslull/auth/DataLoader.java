package com.marcuslull.auth;

import com.marcuslull.auth.models.*;
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
        client.setAuthMethods(List.of(new AuthenticationMethod()));
        client.setGrantTypes(List.of(new GrantType()));
        client.setRedUris(List.of(new Redirect()));
        client.setPostLogRedUris(List.of(new Redirect()));
        client.setAuthorizations(List.of(new ClientAuthorization()));
        clientRepository.save(client);

        clientRepository.findById(51L).ifPresent(result ->
                System.out.println(result.getAvailScopes().get(1).getScope()));
    }
}