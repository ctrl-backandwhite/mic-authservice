package com.backandwhite.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    private Long id;
    private String name;
    private String uniqueName;
    private String description;
    private Boolean enabled;
    @Builder.Default
    private List<Role> roles = new ArrayList<>();
    @Builder.Default
    private List<Permission> permissions = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    public void addRole(List<Role> roles) {
        this.roles.addAll(roles);
    }

    public void removeRole(List<Role> roles) {
        this.roles.removeAll(roles);
    }

    public void addPermission(List<Permission> permissions) {
        this.permissions.addAll(permissions);
    }

    public void removePermission(List<Permission> permissions) {
        this.permissions.removeAll(permissions);
    }

}
