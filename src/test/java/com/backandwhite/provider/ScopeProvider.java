package com.backandwhite.provider;

import com.backandwhite.domain.model.Scope;
import com.backandwhite.infrastructure.db.postgres.entity.ScopeEntity;

public final class ScopeProvider {

    public static final Long READ_ID = 1L;
    public static final String READ_NAME = "Read";
    public static final String READ_UNIQUE_NAME = "SCOPE_READ";
    public static final String READ_DESCRIPTION = "Read scope";
    public static final Boolean READ_ENABLED = true;

    public static final Long WRITE_ID = 2L;
    public static final String WRITE_NAME = "Write";
    public static final String WRITE_UNIQUE_NAME = "SCOPE_WRITE";
    public static final String WRITE_DESCRIPTION = "Write scope";
    public static final Boolean WRITE_ENABLED = false;

    private ScopeProvider() {
        // Utility class.
    }

    public static Scope readScope() {
        return Scope.builder()
                .id(READ_ID)
                .name(READ_NAME)
                .uniqueName(READ_UNIQUE_NAME)
                .description(READ_DESCRIPTION)
                .enabled(READ_ENABLED)
                .build();
    }

    public static Scope writeScope() {
        return Scope.builder()
                .id(WRITE_ID)
                .name(WRITE_NAME)
                .uniqueName(WRITE_UNIQUE_NAME)
                .description(WRITE_DESCRIPTION)
                .enabled(WRITE_ENABLED)
                .build();
    }

    public static Scope scope() {
        return readScope();
    }

    public static ScopeEntity scopeEntity() {
        return ScopeEntity.builder()
                .id(READ_ID)
                .name(READ_NAME)
                .uniqueName(READ_UNIQUE_NAME)
                .description(READ_DESCRIPTION)
                .enabled(READ_ENABLED)
                .build();
    }
}
