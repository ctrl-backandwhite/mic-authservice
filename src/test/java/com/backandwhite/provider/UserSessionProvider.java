package com.backandwhite.provider;

import com.backandwhite.domain.model.UserSession;
import com.backandwhite.infrastructure.db.postgres.entity.UserSessionEntity;
import java.time.Instant;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public final class UserSessionProvider {

    public static final Long SESSION_ID_LONG = 1L;
    public static final Long USER_ID = 1L;
    public static final String SESSION_ID = "abc123session";
    public static final String AUTHORIZATION_ID = "auth-001";
    public static final String DEVICE_INFO = "Chrome · Linux";
    public static final String IP_ADDRESS = "192.168.1.10";
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) Chrome/120.0";
    public static final Instant CREATED_AT = Instant.parse("2026-03-01T10:00:00Z");
    public static final Instant LAST_ACTIVE_AT = Instant.parse("2026-03-01T12:00:00Z");
    public static final Boolean REVOKED = false;

    public static UserSession userSession() {
        return UserSession.builder().id(SESSION_ID_LONG).userId(USER_ID).sessionId(SESSION_ID)
                .authorizationId(AUTHORIZATION_ID).deviceInfo(DEVICE_INFO).ipAddress(IP_ADDRESS).userAgent(USER_AGENT)
                .createdAt(CREATED_AT).lastActiveAt(LAST_ACTIVE_AT).revoked(REVOKED).build();
    }

    public static UserSessionEntity userSessionEntity() {
        return UserSessionEntity.builder().id(SESSION_ID_LONG).userId(USER_ID).sessionId(SESSION_ID)
                .authorizationId(AUTHORIZATION_ID).deviceInfo(DEVICE_INFO).ipAddress(IP_ADDRESS).userAgent(USER_AGENT)
                .createdAt(CREATED_AT).lastActiveAt(LAST_ACTIVE_AT).revoked(REVOKED).build();
    }
}
