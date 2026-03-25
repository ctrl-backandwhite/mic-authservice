package com.backandwhite.domain.model;

import lombok.*;

import java.time.Instant;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scope {

    private Long id;
    private String name;
    private String uniqueName;
    private String description;
    private Boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

}
