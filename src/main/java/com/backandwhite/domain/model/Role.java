package com.backandwhite.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

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
    // Permission is a non-Serializable domain model; this list is only populated
    // when materializing role authorities for the security context, not for
    // session replication. Marked transient (Sonar S1948) since it is rebuilt on
    // load and never round-trips through Java serialization.
    @Builder.Default
    private transient List<Permission> permissions = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @Override
    public @Nullable String getAuthority() {
        return this.uniqueName;
    }
}
