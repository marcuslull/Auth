package com.marcuslull.auth.services;

import com.marcuslull.auth.models.*;
import com.marcuslull.auth.models.enums.AuthType;
import com.marcuslull.auth.models.enums.GrantType;
import com.marcuslull.auth.models.enums.ScopeType;
import com.marcuslull.auth.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientService clientService;

    private RegisteredClient registeredClient;
    private final Client client = new Client();

    @BeforeEach
    void setup() {
        ClientAuthenticationMethod clientAuthenticationMethod = ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        AuthorizationGrantType authorizationGrantType = AuthorizationGrantType.AUTHORIZATION_CODE;
        ClientSettings clientSettings = Mockito.mock(ClientSettings.class);
        TokenSettings tokenSettings = Mockito.mock(TokenSettings.class);

        RegisteredClient.Builder builder = RegisteredClient.withId("1");
        builder.clientId("1").id("1").clientName("name").clientSecret("secret").clientSecretExpiresAt(Instant.now())
                .clientIdIssuedAt(Instant.now()).clientAuthenticationMethod(clientAuthenticationMethod)
                .authorizationGrantType(authorizationGrantType).redirectUri("redirectUri").postLogoutRedirectUri("post")
                .scope("openid").clientSettings(clientSettings).tokenSettings(tokenSettings);
        registeredClient = builder.build();

        Scope scope = new Scope(ScopeType.valueOfLabel("openid"), new Client());
        Auth auth = new Auth(AuthType.valueOfLabel("client_secret_basic"), new Client());
        Grant grant = new Grant(GrantType.valueOfLabel("authorization_code"), new Client());
        Redirect redirect = new Redirect("redirectUri", new Client());
        Redirect postRedirect = new Redirect("post", new Client());
        ClientAuthorization clientAuthorization = Mockito.mock(ClientAuthorization.class);

        client.setId(1L);
        client.setClientId("1");
        client.setName("name");
        client.setSecret("secret");
        client.setClientSettings(clientSettings);
        client.setTokenSettings(tokenSettings);
        client.setAvailableScopes(List.of(scope));
        client.setAuthMethods(List.of(auth));
        client.setAvailableGrants(List.of(grant));
        client.setRedUris(List.of(redirect));
        client.setPostLogRedUris(List.of(postRedirect));
        client.setAuthorizations(List.of(clientAuthorization));
    }

    @Test
    void saveTest() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(clientRepository.save(any(Client.class))).thenReturn(new Client());

        clientService.save(registeredClient);

        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void findByIdTest() {
        when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));

        RegisteredClient result = clientService.findById("1");

        assert result != null;
        assertEquals(registeredClient.getId(), result.getId());
        assertEquals(registeredClient.getClientId(), result.getClientId());
        assertEquals(registeredClient.getClientName(), result.getClientName());
        assertEquals(registeredClient.getClientSecret(), result.getClientSecret());
        assertEquals(registeredClient.getClientSettings(), result.getClientSettings());
        assertEquals(registeredClient.getTokenSettings(), result.getTokenSettings());
        assertEquals(registeredClient.getScopes(), result.getScopes());
        assertEquals(registeredClient.getClientAuthenticationMethods(), result.getClientAuthenticationMethods());
        assertEquals(registeredClient.getAuthorizationGrantTypes(), result.getAuthorizationGrantTypes());
        assertEquals(registeredClient.getRedirectUris(), result.getRedirectUris());
        assertEquals(registeredClient.getPostLogoutRedirectUris(), result.getPostLogoutRedirectUris());

    }

    @Test
    void findByClientIdTest() {
        when(clientRepository.findByClientId(anyString())).thenReturn(Optional.of(client));

        RegisteredClient result = clientService.findByClientId("1");

        assert result != null;
        assertEquals(registeredClient.getId(), result.getId());
        assertEquals(registeredClient.getClientId(), result.getClientId());
        assertEquals(registeredClient.getClientName(), result.getClientName());
        assertEquals(registeredClient.getClientSecret(), result.getClientSecret());
        assertEquals(registeredClient.getClientSettings(), result.getClientSettings());
        assertEquals(registeredClient.getTokenSettings(), result.getTokenSettings());
        assertEquals(registeredClient.getScopes(), result.getScopes());
        assertEquals(registeredClient.getClientAuthenticationMethods(), result.getClientAuthenticationMethods());
        assertEquals(registeredClient.getAuthorizationGrantTypes(), result.getAuthorizationGrantTypes());
        assertEquals(registeredClient.getRedirectUris(), result.getRedirectUris());
        assertEquals(registeredClient.getPostLogoutRedirectUris(), result.getPostLogoutRedirectUris());
    }

}