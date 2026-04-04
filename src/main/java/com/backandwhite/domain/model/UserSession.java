package com.backandwhite.domain.model;

import lombok.*;

import java.time.Instant;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {

    private Long id;
    private Long userId;
    private String sessionId;
    private String authorizationId;
    private String deviceInfo;
    private String ipAddress;
    private String userAgent;
    private Instant createdAt;
    private Instant lastActiveAt;
    private Boolean revoked;
    private Instant revokedAt;
}
