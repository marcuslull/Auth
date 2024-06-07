package com.marcuslull.auth.models;

import com.marcuslull.auth.models.enums.GrantType;
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
@Table(name = "grants")
public class Grant {

    public Grant(GrantType grantType, Client client) {
        this.grantType = grantType;
        this.client = client;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "grant_type")
    @Enumerated(EnumType.ORDINAL)
    private GrantType grantType;

    @ManyToOne
    @JoinColumn(name = "client")
    private Client client;

    public static Grant mapper(GrantType grantType, Client client) {
        return new Grant(grantType, client);
    }
}
