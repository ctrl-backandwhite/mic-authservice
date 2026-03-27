package com.backandwhite.domain.model;

import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role implements GrantedAuthority {

    private Long id;
    private String name;
    private String uniqueName;
    private String description;
    private Boolean enabled;
    @Builder.Default
    private List<Permission> permissions = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @Override
    public @Nullable String getAuthority() {
        return this.uniqueName;
    }
}
