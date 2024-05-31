package com.marcuslull.auth.models;

import com.marcuslull.auth.models.enums.AuthType;
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
@Table(name = "auths")
public class Auth {

    public Auth(AuthType authType, Client client) {
        this.authType = authType;
        this.client = client;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "auth_type")
    @Enumerated
    private AuthType authType;

    @ManyToOne
    @JoinColumn(name = "client")
    private Client client;

    public static Auth mapper(ClientAuthenticationMethod  clientAuthenticationMethod, Client client) {
        return new Auth(AuthType.valueOf(clientAuthenticationMethod.getValue()), client);
    }
}