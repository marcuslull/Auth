package com.marcuslull.auth.models;

import com.marcuslull.auth.models.enums.ScopeType;
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

    public Scope(ScopeType scope, Client client) {
        this.scope = scope;
        this.client = client;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "scope", length = 50)
    @Enumerated
    private ScopeType scope;

    @ManyToOne
    @JoinColumn(name = "client")
    private Client client;

    public static Scope mapper(ScopeType scope, Client client) {
        return new Scope(scope, client);
    }
}
