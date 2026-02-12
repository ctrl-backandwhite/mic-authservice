package com.backandwhite.domain.model;

import lombok.*;




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

}
