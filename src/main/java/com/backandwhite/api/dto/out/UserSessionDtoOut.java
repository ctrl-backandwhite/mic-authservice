package com.backandwhite.api.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionDtoOut {

    private String sessionId;
    private String deviceInfo;
    private String ipAddress;
    private Instant createdAt;
    private Instant lastActiveAt;
}
