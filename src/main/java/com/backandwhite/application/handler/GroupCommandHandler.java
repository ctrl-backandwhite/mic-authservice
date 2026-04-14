package com.backandwhite.application.handler;

import com.backandwhite.domain.model.Group;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.repository.RoleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class GroupCommandHandler {

    private final RoleRepository roleRepository;

    public void validate(Group group) {
        validateRole(group);
    }

    private void validateRole(Group group) {

        if (Objects.isNull(group.getRoles()) || group.getRoles().isEmpty()) {
            log.info("::> No role to associate to group {}", group.getRoles());
            return;
        }

        List<Role> rolesList = new ArrayList<>();
        group.getRoles().forEach(role -> {
            Role response = roleRepository.getById(role.getId());
            rolesList.add(response);
        });
        group.setRoles(rolesList);
    }

}
