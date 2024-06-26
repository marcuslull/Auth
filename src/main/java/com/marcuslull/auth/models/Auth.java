package com.marcuslull.auth.models;

import com.marcuslull.auth.models.enums.AuthType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Enumerated(EnumType.ORDINAL)
    private AuthType authType;

    @ManyToOne
    @JoinColumn(name = "client")
    private Client client;

    public static Auth mapper(AuthType authType, Client client) {
        return new Auth(authType, client);
    }
}