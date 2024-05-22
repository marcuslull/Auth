package com.marcuslull.auth.models;

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
@Table(name = "scopes")
public class Scope {

    public Scope(String scope, Client client) {
        this.scope = scope;
        this.clientId = client;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "scope", nullable = false, length = 50)
    private String scope;

    @ManyToOne
    private Client clientId;

    public static Scope mapper(String scope, Client client) {
        return new Scope(scope, client);
    }
}
