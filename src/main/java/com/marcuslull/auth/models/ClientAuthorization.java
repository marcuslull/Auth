package com.marcuslull.auth.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "authorizations")
public class ClientAuthorization {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "principal_name", nullable = false, length = 200)
    private String principalName;

    @Column(name = "access_token_type", nullable = false, length = 100)
    private String accessTokenType; // type of access token issued in this registration

    @Column(name = "access_token_value", nullable = false)
    private byte[] accessTokenValue;

    @Column(name = "access_token_issued_at", nullable = false)
    private Instant accessTokenIssuedAt;

    @Column(name = "access_token_expires_at", nullable = false)
    private Instant accessTokenExpiresAt;

    @Column(name = "access_token_scopes", nullable = false, length = 1000)
    private String accessTokenScopes; // what scopes this access token grants

    @Column(name = "refresh_token_value")
    private byte[] refreshTokenValue;

    @Column(name = "refresh_token_issued_at")
    private Instant refreshTokenIssuedAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne
    private Client clientId;
}