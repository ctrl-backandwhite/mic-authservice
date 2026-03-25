package com.backandwhite.infrastructure.db.postgres.entity;

import com.backandwhite.common.infrastructure.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@With
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "oauth_clients")
public class OauthClientEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false, unique = true, length = 120)
    private String clientId;

    @Column(name = "clientSecret", nullable = false, length = 120)
    private String clientSecret;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "oauthclient_scopes", joinColumns = @JoinColumn(name = "oauthclient_id"), inverseJoinColumns = @JoinColumn(name = "scope_id"))
    private List<ScopeEntity> scopes = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "oauthclient_redirecturis", joinColumns = @JoinColumn(name = "oauthclient_id"), inverseJoinColumns = @JoinColumn(name = "redirecturi_id"))
    private List<RedirectUriEntity> redirectUris = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "oauthclient_granttypes", joinColumns = @JoinColumn(name = "oauthclient_id"), inverseJoinColumns = @JoinColumn(name = "granttype_id"))
    private List<GrantTypeEntity> grantTypes = new ArrayList<>();

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass())
            return false;
        OauthClientEntity that = (OauthClientEntity) object;
        return Objects.equals(id, that.id)
                && Objects.equals(clientId, that.clientId)
                && Objects.equals(clientSecret, that.clientSecret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientId, clientSecret);
    }
}
