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
@Table(name = "redirects")
public class Redirect {

    public Redirect(String url, Client client) {
        this.url = url;
        this.client = client;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "client")
    private Client client;

    public static Redirect mapper(String url, Client client) {
        return new Redirect(url, client);
    }
}
