package com.marcuslull.auth.services;

import com.marcuslull.auth.models.*;
import com.marcuslull.auth.models.enums.GrantType;
import com.marcuslull.auth.models.enums.ScopeType;
import com.marcuslull.auth.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class ClientService implements RegisteredClientRepository {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        log.info("AUTH_START: ClientService");
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        log.warn("AUTH_CLIENT: ClientService.save({}) - persisting new client", registeredClient.getClientName());

        // TODO: Move to helper or class
        // convert from Spring default RegisteredClient to my custom Client entity and then persist
        Client client = new Client();
        client.setClientId(registeredClient.getClientId());
        client.setName(registeredClient.getClientName());
        client.setSecret(passwordEncoder.encode(registeredClient.getClientSecret()));
        client.setClientSettings(registeredClient.getClientSettings());
        client.setTokenSettings(registeredClient.getTokenSettings());
        client.setAvailableScopes(registeredClient
                .getScopes().stream().map(scope -> Scope.mapper(ScopeType.valueOfLabel(scope), client)).toList());
        client.setAuthMethods(registeredClient
                .getClientAuthenticationMethods().stream().map(auth -> Auth.mapper(auth, client)).toList());
        client.setAvailableGrants(registeredClient
                .getAuthorizationGrantTypes().stream().map(grant -> Grant.mapper(GrantType.valueOfLabel(grant.getValue()), client)).toList());
        client.setRedUris(registeredClient
                .getRedirectUris().stream().map(url -> Redirect.mapper(url, client)).toList());
        client.setPostLogRedUris(registeredClient
                .getPostLogoutRedirectUris().stream().map(url -> Redirect.mapper(url, client)).toList());
        client.setAuthorizations(new ArrayList<>());

        clientRepository.save(client);
    }

    @Override
    public RegisteredClient findById(String id) {
        log.warn("AUTH_CLIENT: ClientService.findById({}) - finding client", id);
        // Convert from custom Client to Spring RegisteredClient
        Optional<Client> optionalClient = clientRepository.findById(Long.valueOf(id));
        return optionalClient.map(Client::mapper).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        log.warn("AUTH_CLIENT: ClientService.findByClientId({}) - finding client", clientId);
        // Convert from custom Client to Spring RegisteredClient
        Optional<Client> optionalClient = clientRepository.findByClientId(clientId);
        return optionalClient.map(Client::mapper).orElseThrow(EntityNotFoundException::new);
    }
}
