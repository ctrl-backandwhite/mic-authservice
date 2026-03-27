package com.backandwhite.provider;

import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public final class AuditProvider {

    public static final Instant CREATED_AT = Instant.parse("2026-02-16T10:15:30Z");
    public static final Instant UPDATED_AT = Instant.parse("2026-02-16T11:05:00Z");
    public static final String CREATED_BY = "admin@dominio.com";
    public static final String UPDATED_BY = "usuario@dominio.com";
    
}
