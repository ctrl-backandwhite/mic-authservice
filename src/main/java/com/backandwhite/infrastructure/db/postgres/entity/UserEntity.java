package com.backandwhite.infrastructure.db.postgres.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@With
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 60)
    private String name;

    @Column(name = "last_name", length = 60)
    private String lastName;

    @Column(name = "nick_name", length = 60, unique = true)
    private String nickName;

    @Column(name = "email", unique = true, length = 120)
    private String email;

    @Column(name = "password", length = 250)
    private String password;

    @Column(name = "enabled", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean enabled;

    @Column(name = "account_non_expired", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean accountNonExpired;

    @Column(name = "account_non_locked", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean accountNonLocked;

    @Column(name = "credentials_non_expired", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean credentialsNonExpired;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_scopes",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "scope_id")
    )
    private List<ScopeEntity> scopes = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<RoleEntity> roles = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_groups",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private List<GroupEntity> groups = new ArrayList<>();

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        UserEntity that = (UserEntity) object;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(lastName, that.lastName)
                && Objects.equals(nickName, that.nickName)
                && Objects.equals(email, that.email)
                && Objects.equals(password, that.password)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(accountNonExpired, that.accountNonExpired)
                && Objects.equals(accountNonLocked, that.accountNonLocked)
                && Objects.equals(credentialsNonExpired, that.credentialsNonExpired);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastName, nickName, email, password, enabled, accountNonExpired, accountNonLocked, credentialsNonExpired);
    }
}
