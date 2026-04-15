package com.backandwhite.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.provider.PermissionProvider;
import com.backandwhite.provider.RoleProvider;
import java.util.List;
import org.junit.jupiter.api.Test;

class GroupTest {

    @Test
    void addRole_appendsToExistingRoles() {
        Group group = Group.builder().build();
        Role role = RoleProvider.adminRole();

        group.addRole(List.of(role));

        assertThat(group.getRoles()).containsExactly(role);
    }

    @Test
    void removeRole_removesFromExistingRoles() {
        Role admin = RoleProvider.adminRole();
        Role user = RoleProvider.userRole();
        Group group = Group.builder().build();
        group.addRole(List.of(admin, user));

        group.removeRole(List.of(admin));

        assertThat(group.getRoles()).containsExactly(user);
    }

    @Test
    void addPermission_appendsToExistingPermissions() {
        Group group = Group.builder().build();
        Permission permission = PermissionProvider.permission();

        group.addPermission(List.of(permission));

        assertThat(group.getPermissions()).containsExactly(permission);
    }

    @Test
    void removePermission_removesFromExistingPermissions() {
        Permission permission = PermissionProvider.permission();
        Group group = Group.builder().build();
        group.addPermission(List.of(permission));

        group.removePermission(List.of(permission));

        assertThat(group.getPermissions()).isEmpty();
    }
}
