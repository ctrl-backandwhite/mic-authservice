package com.backandwhite.domain.model;

import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;

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
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @Override
    public @Nullable String getAuthority() {
        return this.uniqueName;
    }
}
