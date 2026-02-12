package com.backandwhite.domain.model;

import lombok.*;




@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedirectUri {

    private Long id;
    private String name;
    private String value;
    private Boolean enabled;

}
