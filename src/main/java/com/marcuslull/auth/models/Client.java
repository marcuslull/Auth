package com.marcuslull.auth.models;

import com.marcuslull.auth.models.enums.ScopeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "clients")
public class Client implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "client_id", length = 100, unique = true)
    private String clientId;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "secret", length = 500)
    private String secret;

    @Column(name = "client_settings")
    private ClientSettings clientSettings;

    @Column(name = "token_settings")
    private TokenSettings tokenSettings;

    @OneToMany(mappedBy = "client", cascade = CascadeType.PERSIST)
    @Column(name = "avail_scopes", length = 2000)
    private List<Scope> availScopes = new ArrayList<>(); // the permission scopes this client can offer to the user

    public void addScope(ScopeType scopeType) {
        if(this.availScopes.stream().noneMatch(scope -> scope.getScope() == scopeType)) {
            this.availScopes.add(new Scope(scopeType, this));
        }
    }

    @OneToMany(mappedBy = "clientId")
    @Column(name = "auth_methods", length = 2000)
    private List<AuthenticationMethod> authMethods; // the authentication methods this client can use to authenticate with this auth server

    @OneToMany(mappedBy = "clientId")
    @Column(name = "grant_types", length = 2000)
    private List<GrantType> grantTypes; // the methods available to retrieve a token from this auth server

    @OneToMany(mappedBy = "clientId")
    @Column(name = "red_uris", length = 2000)
    private List<Redirect> redUris; // where the user will be redirected to after they log in

    @OneToMany(mappedBy = "clientId")
    @Column(name = "post_log_red_uris", length = 2000)
    private List<Redirect> postLogRedUris; // where the user will be redirected after they log out

    @OneToMany(mappedBy = "clientId")
    @Column(name = "authorizations", length = 2000)
    private List<ClientAuthorization> authorizations; // list of current authorizations

    public static RegisteredClient mapper(Client client) {
        return RegisteredClient.withId(String.valueOf(client.getId()))
                .clientId(client.getClientId())
                .clientName(client.getName())
                .clientSecret(client.getSecret())
                .clientSettings(client.getClientSettings())
                .tokenSettings(client.getTokenSettings())
                .scopes(convertScope(client.getAvailScopes()))
                .clientAuthenticationMethods(convertAuthMethod(client.getAuthMethods()))
                .authorizationGrantTypes(convertGrantType(client.getGrantTypes()))
                .redirectUris(convertRedirectUri(client.getRedUris()))
                .postLogoutRedirectUris(convertRedirectUri(client.getPostLogRedUris()))
                .build();
    }

    private static Consumer<Set<String>> convertScope(List<Scope> scopes) {
        return consumer -> {
            for (Scope scope : scopes) {
                consumer.add(scope.getScope().label);
            }
        };
    }

    private static Consumer<Set<ClientAuthenticationMethod>> convertAuthMethod(List<AuthenticationMethod> authenticationMethods) {
        return consumer -> {
            for (AuthenticationMethod auth : authenticationMethods) {
                consumer.add(new ClientAuthenticationMethod(auth.getAuthMethod()));
            }
        };
    }

    private static Consumer<Set<AuthorizationGrantType>> convertGrantType(List<GrantType> grantTypes) {
        return consumer -> {
            for (GrantType grant : grantTypes) {
                consumer.add(new AuthorizationGrantType(grant.getGrantType()));
            }
        };
    }

    private static Consumer<Set<String>> convertRedirectUri(List<Redirect> redirects) {
        return consumer -> {
            for (Redirect redirect : redirects) {
                consumer.add(redirect.getUrl());
            }
        };
    }
}