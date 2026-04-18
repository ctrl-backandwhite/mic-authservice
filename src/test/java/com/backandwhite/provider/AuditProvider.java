package com.backandwhite.provider;

import java.time.Instant;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public final class AuditProvider {

    public static final Instant CREATED_AT = Instant.parse("2026-02-16T10:15:30Z");
    public static final Instant UPDATED_AT = Instant.parse("2026-02-16T11:05:00Z");
    public static final String CREATED_BY = "admin@domain.com";
    public static final String UPDATED_BY = "user@domain.com";

}
