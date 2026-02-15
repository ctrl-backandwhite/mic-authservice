package com.backandwhite.provider;

import com.backandwhite.api.dto.out.RoleDtoOut;
import com.backandwhite.api.dto.in.RoleDtoIn;

import com.backandwhite.domain.model.Role;
import com.backandwhite.infrastructure.db.postgres.entity.RoleEntity;

public final class RoleProvider {

    public static final Long ADMIN_ID = 1L;
    public static final String ADMIN_NAME = "Admin";
    public static final String ADMIN_UNIQUE_NAME = "ROLE_ADMIN";
    public static final String ADMIN_DESCRIPTION = "Admin role";
    public static final Boolean ADMIN_ENABLED = true;

    public static final Long USER_ID = 2L;
    public static final String USER_NAME = "User";
    public static final String USER_UNIQUE_NAME = "ROLE_USER";
    public static final String USER_DESCRIPTION = "User role";
    public static final Boolean USER_ENABLED = false;

    public static final Long ROLE_ID = 3L;
    public static final String ROLE_NAME = "Support";
    public static final String ROLE_UNIQUE_NAME = "ROLE_SUPPORT";
    public static final String ROLE_DESCRIPTION = "Support role";
    public static final Boolean ROLE_ENABLED = true;

    private RoleProvider() {
        // Utility class.
    }

    public static Role adminRole() {
        return Role.builder()
                .id(ADMIN_ID)
                .name(ADMIN_NAME)
                .uniqueName(ADMIN_UNIQUE_NAME)
                .description(ADMIN_DESCRIPTION)
                .enabled(ADMIN_ENABLED)
                .build();
    }

    public static Role userRole() {
        return Role.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .uniqueName(USER_UNIQUE_NAME)
                .description(USER_DESCRIPTION)
                .enabled(USER_ENABLED)
                .build();
    }

    public static Role role() {
        return Role.builder()
                .id(ROLE_ID)
                .name(ROLE_NAME)
                .uniqueName(ROLE_UNIQUE_NAME)
                .description(ROLE_DESCRIPTION)
                .enabled(ROLE_ENABLED)
                .build();
    }

    public static RoleEntity roleEntity() {
        return RoleEntity.builder()
                .id(ROLE_ID)
                .name(ROLE_NAME)
                .uniqueName(ROLE_UNIQUE_NAME)
                .description(ROLE_DESCRIPTION)
                .enabled(ROLE_ENABLED)
                .build();
    }

    public static RoleEntity adminRoleEntity() {
        return RoleEntity.builder()
                .name(ADMIN_NAME)
                .uniqueName(ADMIN_UNIQUE_NAME)
                .description(ADMIN_DESCRIPTION)
                .enabled(ADMIN_ENABLED)
                .build();
    }

    public static RoleEntity userRoleEntity() {
        return RoleEntity.builder()
                .name(USER_NAME)
                .uniqueName(USER_UNIQUE_NAME)
                .description(USER_DESCRIPTION)
                .enabled(USER_ENABLED)
                .build();
    }

    public static RoleDtoOut adminRoleDtoOut(Long id) {
        return RoleDtoOut.builder()
                .id(id)
                .name(ADMIN_NAME)
                .uniqueName(ADMIN_UNIQUE_NAME)
                .description(ADMIN_DESCRIPTION)
                .enabled(ADMIN_ENABLED)
                .build();
    }

    public static RoleDtoOut userRoleDtoOut(Long id) {
        return RoleDtoOut.builder()
                .id(id)
                .name(USER_NAME)
                .uniqueName(USER_UNIQUE_NAME)
                .description(USER_DESCRIPTION)
                .enabled(USER_ENABLED)
                .build();
    }

    public static RoleDtoOut supportRoleDtoOut(Long id) {
        return RoleDtoOut.builder()
                .id(id)
                .name(ROLE_NAME)
                .uniqueName(ROLE_UNIQUE_NAME)
                .description(ROLE_DESCRIPTION)
                .enabled(ROLE_ENABLED)
                .build();
    }

    public static RoleDtoIn adminRoleDtoIn() {
        return RoleDtoIn.builder()
                .name(ADMIN_NAME)
                .uniqueName(ADMIN_UNIQUE_NAME)
                .description(ADMIN_DESCRIPTION)
                .enabled(ADMIN_ENABLED)
                .build();
    }

    public static RoleDtoIn userRoleDtoIn() {
        return RoleDtoIn.builder()
                .name(USER_NAME)
                .uniqueName(USER_UNIQUE_NAME)
                .description(USER_DESCRIPTION)
                .enabled(USER_ENABLED)
                .build();
    }

    public static RoleDtoIn supportRoleDtoIn() {
        return RoleDtoIn.builder()
                .name(ROLE_NAME)
                .uniqueName(ROLE_UNIQUE_NAME)
                .description(ROLE_DESCRIPTION)
                .enabled(ROLE_ENABLED)
                .build();
    }
}
