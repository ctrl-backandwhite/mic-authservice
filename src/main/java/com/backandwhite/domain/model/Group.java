package com.backandwhite.domain.model;

import lombok.*;


import java.util.List;
import java.util.ArrayList;


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

    public void addRole(List<Role> roles) {
        this.roles.addAll(roles);
    }

    public void removeRole(List<Role> roles) {
        this.roles.removeAll(roles);
    }

}
