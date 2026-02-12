package com.backandwhite.domain.model;

import lombok.*;




@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrantType {

    private Long id;
    private String value;
    private Boolean enabled;

}
