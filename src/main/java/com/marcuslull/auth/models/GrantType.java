package com.marcuslull.auth.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "grants")
public class GrantType {

    public GrantType(String grantType, Client client) {
        this.grantType = grantType;
        this.clientId = client;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "grant_type", nullable = false, length = 50)
    private String grantType;

    @ManyToOne
    private Client clientId;

    public static GrantType mapper(AuthorizationGrantType authorizationGrantType, Client client) {
        return new GrantType(authorizationGrantType.getValue(), client);
    }
}
