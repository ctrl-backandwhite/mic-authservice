package com.backandwhite.application.handler;

import com.backandwhite.domain.model.User;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.repository.RoleRepository;
import com.backandwhite.domain.model.Group;
import com.backandwhite.domain.repository.GroupRepository;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@Component
@AllArgsConstructor
public class UserCommandHandler {

    private final RoleRepository roleRepository;
    private final GroupRepository groupRepository;

    public void validate(User user) {
        validateRole(user);
        validateGroup(user);
    }


    private void validateRole(User user) {

        if (Objects.isNull(user.getRoles()) || user.getRoles().isEmpty()) {
            log.info("::> No role to associate to user {}", user.getRoles());
            return;
        }

        List<Role> rolesList = new ArrayList<>();
        user.getRoles().forEach(role -> {
            Role response = roleRepository.getById(role.getId());
            rolesList.add(response);
        });
        user.setRoles(rolesList);
    }


    private void validateGroup(User user) {

        if (Objects.isNull(user.getGroups()) || user.getGroups().isEmpty()) {
            log.info("::> No group to associate to user {}", user.getGroups());
            return;
        }

        List<Group> groupsList = new ArrayList<>();
        user.getGroups().forEach(group -> {
            Group response = groupRepository.getById(group.getId());
            groupsList.add(response);
        });
        user.setGroups(groupsList);
    }

}
