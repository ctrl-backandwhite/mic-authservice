package com.backandwhite.provider;

import com.backandwhite.api.dto.in.PermissionDtoIn;
import com.backandwhite.api.dto.out.PermissionDtoOut;
import com.backandwhite.domain.model.Permission;
import com.backandwhite.infrastructure.db.postgres.entity.PermissionEntity;

public final class PermissionProvider {
    public static final Long PERMISSION_ID = 1L;
    public static final String PERMISSION_NAME = "Leer usuarios";
    public static final String PERMISSION_UNIQUE_NAME = "READ_USERS";
    public static final String PERMISSION_DESCRIPTION = "Permite listar usuarios";
    public static final Boolean PERMISSION_ENABLED = true;

    private PermissionProvider() {
    }

    public static Permission permission() {
        return Permission.builder().id(PERMISSION_ID).name(PERMISSION_NAME).uniqueName(PERMISSION_UNIQUE_NAME)
                .description(PERMISSION_DESCRIPTION).enabled(PERMISSION_ENABLED).createdAt(AuditProvider.CREATED_AT)
                .updatedAt(AuditProvider.UPDATED_AT).createdBy(AuditProvider.CREATED_BY)
                .updatedBy(AuditProvider.UPDATED_BY).build();
    }

    public static PermissionEntity permissionEntity() {
        return PermissionEntity.builder().id(PERMISSION_ID).name(PERMISSION_NAME).uniqueName(PERMISSION_UNIQUE_NAME)
                .description(PERMISSION_DESCRIPTION).enabled(PERMISSION_ENABLED).createdAt(AuditProvider.CREATED_AT)
                .updatedAt(AuditProvider.UPDATED_AT).createdBy(AuditProvider.CREATED_BY)
                .updatedBy(AuditProvider.UPDATED_BY).build();
    }

    public static PermissionDtoIn permissionDtoIn() {
        return PermissionDtoIn.builder().name(PERMISSION_NAME).uniqueName(PERMISSION_UNIQUE_NAME)
                .description(PERMISSION_DESCRIPTION).enabled(PERMISSION_ENABLED).build();
    }

    public static PermissionDtoOut permissionDtoOut(Long id) {
        return PermissionDtoOut.builder().id(id).name(PERMISSION_NAME).uniqueName(PERMISSION_UNIQUE_NAME)
                .description(PERMISSION_DESCRIPTION).enabled(PERMISSION_ENABLED).createdAt(AuditProvider.CREATED_AT)
                .updatedAt(AuditProvider.UPDATED_AT).createdBy(AuditProvider.CREATED_BY)
                .updatedBy(AuditProvider.UPDATED_BY).build();
    }
}
