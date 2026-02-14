package com.backandwhite.provider;

import com.backandwhite.domain.model.GrantType;
import com.backandwhite.infrastructure.db.postgres.entity.GrantTypeEntity;

public final class GrantTypeProvider {

    public static final Long GRANT_TYPE_ID = 1L;
    public static final String GRANT_TYPE_VALUE = "authorization_code";
    public static final Boolean GRANT_TYPE_ENABLED = true;

    public static final Long OTHER_GRANT_TYPE_ID = 2L;
    public static final String OTHER_GRANT_TYPE_VALUE = "client_credentials";
    public static final Boolean OTHER_GRANT_TYPE_ENABLED = false;

    private GrantTypeProvider() {
        // Utility class.
    }

    public static GrantType grantType() {
        return GrantType.builder()
                .id(GRANT_TYPE_ID)
                .value(GRANT_TYPE_VALUE)
                .enabled(GRANT_TYPE_ENABLED)
                .build();
    }

    public static GrantType otherGrantType() {
        return GrantType.builder()
                .id(OTHER_GRANT_TYPE_ID)
                .value(OTHER_GRANT_TYPE_VALUE)
                .enabled(OTHER_GRANT_TYPE_ENABLED)
                .build();
    }

    public static GrantTypeEntity grantTypeEntity() {
        return GrantTypeEntity.builder()
                .id(GRANT_TYPE_ID)
                .value(GRANT_TYPE_VALUE)
                .enabled(GRANT_TYPE_ENABLED)
                .build();
    }
}
