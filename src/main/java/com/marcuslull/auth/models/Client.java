package com.marcuslull.auth.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "clients")
public class Client implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "client_id", nullable = false, length = 100, unique = true)
    private String clientId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "secret", nullable = false, length = 500)
    private String secret;

    @Column(name = "client_settings", nullable = false, length = 2000)
    private String clientSettings;

    @Column(name = "token_settings", nullable = false, length = 2000)
    private String tokenSettings;

    @OneToMany(mappedBy = "clientId")
    @Column(name = "avail_scopes", nullable = false, length = 2000)
    private List<Scope> availScopes; // the permission scopes this client can offer to the user

    @OneToMany(mappedBy = "clientId")
    @Column(name = "auth_methods", nullable = false, length = 2000)
    private List<AuthenticationMethod> authMethods; // the authentication methods this client can use to authenticate with this auth server

    @OneToMany(mappedBy = "clientId")
    @Column(name = "grant_types", nullable = false, length = 2000)
    private List<GrantType> grantTypes; // the methods available to retrieve a token from this auth server

    @OneToMany(mappedBy = "clientId")
    @Column(name = "red_uris", nullable = false, length = 2000)
    private List<Redirects> redUris; // where the user will be redirected to after they log in

    @OneToMany(mappedBy = "clientId")
    @Column(name = "post_log_red_uris", nullable = false, length = 2000)
    private List<Redirects> postLogRedUris; // where the user will be redirected after they log out

    @OneToMany(mappedBy = "clientId")
    @Column(name = "authorizations", nullable = false, length = 2000)
    private List<ClientAuthorization> oauth2AuthorizedClients; // list of current authorizations
}