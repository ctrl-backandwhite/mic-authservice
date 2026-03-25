package com.backandwhite.infrastructure.db.postgres.entity;

import com.backandwhite.common.infrastructure.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@With
@Getter
@Setter
@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class RoleEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @Column(name = "unique_name", length = 60, unique = true)
    private String uniqueName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean enabled;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private List<GroupEntity> groups = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private List<PermissionEntity> permissions = new ArrayList<>();

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass())
            return false;
        RoleEntity that = (RoleEntity) object;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(uniqueName, that.uniqueName)
                && Objects.equals(description, that.description)
                && Objects.equals(enabled, that.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, uniqueName, description, enabled);
    }
}