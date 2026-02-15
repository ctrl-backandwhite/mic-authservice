package com.backandwhite.provider;

import com.backandwhite.api.dto.in.GroupDtoIn;
import com.backandwhite.api.dto.out.GroupDtoOut;
import com.backandwhite.domain.model.Group;
import com.backandwhite.infrastructure.db.postgres.entity.GroupEntity;

import java.util.List;

public final class GroupProvider {

    public static final Long ADMIN_ID = 1L;
    public static final String ADMIN_NAME = "Admins";
    public static final String ADMIN_UNIQUE_NAME = "GROUP_ADMINS";
    public static final String ADMIN_DESCRIPTION = "Admin group";
    public static final Boolean ADMIN_ENABLED = true;

    public static final Long USER_ID = 2L;
    public static final String USER_NAME = "Users";
    public static final String USER_UNIQUE_NAME = "GROUP_USERS";
    public static final String USER_DESCRIPTION = "User group";
    public static final Boolean USER_ENABLED = false;

    public static final Long GROUP_ID = 3L;
    public static final String GROUP_NAME = "Support";
    public static final String GROUP_UNIQUE_NAME = "GROUP_SUPPORT";
    public static final String GROUP_DESCRIPTION = "Support group";
    public static final Boolean GROUP_ENABLED = true;

    private GroupProvider() {
    }

    public static Group adminGroup() {
        return Group.builder()
                .id(ADMIN_ID)
                .name(ADMIN_NAME)
                .uniqueName(ADMIN_UNIQUE_NAME)
                .description(ADMIN_DESCRIPTION)
                .enabled(ADMIN_ENABLED)
                .roles(List.of(RoleProvider.adminRole()))
                .build();
    }

    public static Group userGroup() {
        return Group.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .uniqueName(USER_UNIQUE_NAME)
                .description(USER_DESCRIPTION)
                .enabled(USER_ENABLED)
                .roles(List.of(RoleProvider.userRole()))
                .build();
    }

    public static Group group() {
        return Group.builder()
                .id(GROUP_ID)
                .name(GROUP_NAME)
                .uniqueName(GROUP_UNIQUE_NAME)
                .description(GROUP_DESCRIPTION)
                .enabled(GROUP_ENABLED)
                .roles(List.of(RoleProvider.role()))
                .build();
    }

    public static GroupEntity groupEntity() {
        return GroupEntity.builder()
                .id(GROUP_ID)
                .name(GROUP_NAME)
                .uniqueName(GROUP_UNIQUE_NAME)
                .description(GROUP_DESCRIPTION)
                .enabled(GROUP_ENABLED)
                .roles(List.of(RoleProvider.roleEntity()))
                .build();
    }

    public static GroupEntity adminGroupEntity() {
        return GroupEntity.builder()
                .id(ADMIN_ID)
                .name(ADMIN_NAME)
                .uniqueName(ADMIN_UNIQUE_NAME)
                .description(ADMIN_DESCRIPTION)
                .enabled(ADMIN_ENABLED)
                .roles(List.of(RoleProvider.adminRoleEntity()))
                .build();
    }

    public static GroupEntity userGroupEntity() {
        return GroupEntity.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .uniqueName(USER_UNIQUE_NAME)
                .description(USER_DESCRIPTION)
                .enabled(USER_ENABLED)
                .roles(List.of(RoleProvider.userRoleEntity()))
                .build();
    }

    public static GroupDtoIn adminGroupDtoIn() {
        return GroupDtoIn.builder()
                .name(ADMIN_NAME)
                .uniqueName(ADMIN_UNIQUE_NAME)
                .description(ADMIN_DESCRIPTION)
                .enabled(ADMIN_ENABLED)
                .roleIds(List.of(RoleProvider.ADMIN_ID))
                .build();
    }

    public static GroupDtoIn userGroupDtoIn() {
        return GroupDtoIn.builder()
                .name(USER_NAME)
                .uniqueName(USER_UNIQUE_NAME)
                .description(USER_DESCRIPTION)
                .enabled(USER_ENABLED)
                .roleIds(List.of(RoleProvider.USER_ID))
                .build();
    }

    public static GroupDtoOut adminGroupDtoOut(Long id) {
        return GroupDtoOut.builder()
                .id(id)
                .name(ADMIN_NAME)
                .uniqueName(ADMIN_UNIQUE_NAME)
                .description(ADMIN_DESCRIPTION)
                .enabled(ADMIN_ENABLED)
                .roles(List.of(RoleProvider.adminRoleDtoOut(null)))
                .build();
    }

    public static GroupDtoOut userGroupDtoOut(Long id) {
        return GroupDtoOut.builder()
                .id(id)
                .name(USER_NAME)
                .uniqueName(USER_UNIQUE_NAME)
                .description(USER_DESCRIPTION)
                .enabled(USER_ENABLED)
                .roles(List.of(RoleProvider.userRoleDtoOut(null)))
                .build();
    }
}
