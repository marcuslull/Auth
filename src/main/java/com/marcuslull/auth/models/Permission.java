package com.marcuslull.auth.models;

import com.marcuslull.auth.models.enums.PermType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "permissions")
public class Permission implements GrantedAuthority {

    public Permission(PermType permType, User user) {
        this.permType = permType;
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "perm")
    @Enumerated(EnumType.ORDINAL)
    private PermType permType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String getAuthority() {
        return this.permType.label;
    }
}
