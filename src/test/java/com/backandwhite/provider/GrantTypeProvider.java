package com.backandwhite.provider;

import com.backandwhite.api.dto.in.GrantTypeDtoIn;
import com.backandwhite.api.dto.out.GrantTypeDtoOut;
import com.backandwhite.domain.model.GrantType;
import com.backandwhite.infrastructure.db.postgres.entity.GrantTypeEntity;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public final class GrantTypeProvider {

    public static final Long GRANT_TYPE_ID = 1L;
    public static final String GRANT_TYPE_VALUE = "authorization_code";
    public static final Boolean GRANT_TYPE_ENABLED = true;

    public static final Long OTHER_GRANT_TYPE_ID = 2L;
    public static final String OTHER_GRANT_TYPE_VALUE = "client_credentials";
    public static final Boolean OTHER_GRANT_TYPE_ENABLED = false;

    public static GrantType grantType() {
        return GrantType.builder().id(GRANT_TYPE_ID).value(GRANT_TYPE_VALUE).enabled(GRANT_TYPE_ENABLED)
                .createdAt(AuditProvider.CREATED_AT).updatedAt(AuditProvider.UPDATED_AT)
                .createdBy(AuditProvider.CREATED_BY).updatedBy(AuditProvider.UPDATED_BY).build();
    }

    public static GrantType otherGrantType() {
        return GrantType.builder().id(OTHER_GRANT_TYPE_ID).value(OTHER_GRANT_TYPE_VALUE)
                .enabled(OTHER_GRANT_TYPE_ENABLED).createdAt(AuditProvider.CREATED_AT)
                .updatedAt(AuditProvider.UPDATED_AT).createdBy(AuditProvider.CREATED_BY)
                .updatedBy(AuditProvider.UPDATED_BY).build();
    }

    public static GrantTypeEntity grantTypeEntity() {
        return GrantTypeEntity.builder().id(GRANT_TYPE_ID).value(GRANT_TYPE_VALUE).enabled(GRANT_TYPE_ENABLED)
                .createdAt(AuditProvider.CREATED_AT).updatedAt(AuditProvider.UPDATED_AT)
                .createdBy(AuditProvider.CREATED_BY).updatedBy(AuditProvider.UPDATED_BY).build();
    }

    public static GrantTypeEntity otherGrantTypeEntity() {
        return GrantTypeEntity.builder().id(OTHER_GRANT_TYPE_ID).value(OTHER_GRANT_TYPE_VALUE)
                .enabled(OTHER_GRANT_TYPE_ENABLED).createdAt(AuditProvider.CREATED_AT)
                .updatedAt(AuditProvider.UPDATED_AT).createdBy(AuditProvider.CREATED_BY)
                .updatedBy(AuditProvider.UPDATED_BY).build();
    }

    public static GrantTypeDtoIn grantTypeDtoIn() {
        return GrantTypeDtoIn.builder().value(GRANT_TYPE_VALUE).enabled(GRANT_TYPE_ENABLED).build();
    }

    public static GrantTypeDtoIn otherGrantTypeDtoIn() {
        return GrantTypeDtoIn.builder().value(OTHER_GRANT_TYPE_VALUE).enabled(OTHER_GRANT_TYPE_ENABLED).build();
    }

    public static GrantTypeDtoOut grantTypeDtoOut(Long id) {
        return GrantTypeDtoOut.builder().id(id).value(GRANT_TYPE_VALUE).enabled(GRANT_TYPE_ENABLED)
                .createdAt(AuditProvider.CREATED_AT).updatedAt(AuditProvider.UPDATED_AT)
                .createdBy(AuditProvider.CREATED_BY).updatedBy(AuditProvider.UPDATED_BY).build();
    }

    public static GrantTypeDtoOut otherGrantTypeDtoOut(Long id) {
        return GrantTypeDtoOut.builder().id(id).value(OTHER_GRANT_TYPE_VALUE).enabled(OTHER_GRANT_TYPE_ENABLED)
                .createdAt(AuditProvider.CREATED_AT).updatedAt(AuditProvider.UPDATED_AT)
                .createdBy(AuditProvider.CREATED_BY).updatedBy(AuditProvider.UPDATED_BY).build();
    }
}
