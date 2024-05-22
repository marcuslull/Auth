package com.marcuslull.auth.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "auth_method")
public class AuthenticationMethod {

    public AuthenticationMethod(String clientAuthenticationMethod, Client client) {
        this.authMethod = clientAuthenticationMethod;
        this.clientId = client;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "auth_method", nullable = false, length = 50)
    private String authMethod;

    @ManyToOne
    private Client clientId;

    public static AuthenticationMethod mapper(ClientAuthenticationMethod  clientAuthenticationMethod, Client client) {
        return new AuthenticationMethod(clientAuthenticationMethod.getValue(), client);
    }
}