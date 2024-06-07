package com.marcuslull.auth.models;

import com.marcuslull.auth.models.enums.AuthType;
import com.marcuslull.auth.models.enums.GrantType;
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
import java.util.Objects;
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

    // the permissions this client can offer
    @OneToMany(mappedBy = "client", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Scope> availableScopes = new ArrayList<>();

    public void addScope(ScopeType scopeType) {
        if(this.availableScopes.stream().noneMatch(scope -> scope.getScope() == scopeType)) {
            this.availableScopes.add(new Scope(scopeType, this));
        }
    }

    // the methods available to retrieve a token from this auth server
    @OneToMany(mappedBy = "client", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Grant> availableGrants = new ArrayList<>();

    public void addGrant(GrantType grantType) {
        if(this.availableGrants.stream().noneMatch(grant -> grant.getGrantType() == grantType)) {
            this.availableGrants.add(new Grant(grantType, this));
        }
    }

    // the authentication methods this client can use to authenticate with this auth server
    @OneToMany(mappedBy = "client", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Auth> authMethods = new ArrayList<>();

    public void addAuth(AuthType authType) {
        if (this.authMethods.stream().noneMatch(auth -> Objects.equals(auth.getAuthType(), authType))) {
            this.authMethods.add(new Auth(authType, this));
        }
    }

    // where the user will be redirected to after they log in
    @OneToMany(mappedBy = "client", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Redirect> redUris = new ArrayList<>();

    public void addPreRedirect(String redirect) {
        if (this.redUris.stream().noneMatch(redir -> redir.getUrl().equals(redirect))) {
            this.redUris.add(new Redirect(redirect, this));
        }
    }

    // where the user will be redirected after they log out
    @OneToMany(mappedBy = "client", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Redirect> postLogRedUris = new ArrayList<>();

    public void addPostRedirect(String redirect) {
        if (this.postLogRedUris.stream().noneMatch(redir -> redir.getUrl().equals(redirect))) {
            this.postLogRedUris.add(new Redirect(redirect, this));
        }
    }

    // list of current authorizations
    @OneToMany(mappedBy = "clientId")
    private List<ClientAuthorization> authorizations;

    public static RegisteredClient mapper(Client client) {
        return RegisteredClient.withId(String.valueOf(client.getId()))
                .clientId(client.getClientId())
                .clientName(client.getName())
                .clientSecret(client.getSecret())
                .clientSettings(client.getClientSettings())
                .tokenSettings(client.getTokenSettings())
                .scopes(convertScope(client.getAvailableScopes()))
                .clientAuthenticationMethods(convertAuthMethod(client.getAuthMethods()))
                .authorizationGrantTypes(convertGrantType(client.getAvailableGrants()))
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

    private static Consumer<Set<ClientAuthenticationMethod>> convertAuthMethod(List<Auth> auths) {
        return consumer -> {
            for (Auth auth : auths) {
                consumer.add(new ClientAuthenticationMethod(auth.getAuthType().label));
            }
        };
    }

    private static Consumer<Set<AuthorizationGrantType>> convertGrantType(List<Grant> grants) {
        return consumer -> {
            for (Grant grant : grants) {
                consumer.add(new AuthorizationGrantType(grant.getGrantType().label));
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