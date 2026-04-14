package com.backandwhite.domain.repository;

import com.backandwhite.domain.model.UserSession;
import java.util.List;

public interface UserSessionRepository {

    UserSession save(UserSession model);

    List<UserSession> findActiveByUserId(Long userId);

    UserSession findBySessionId(String sessionId);

    void revokeSession(String sessionId);

    void updateLastActiveAt(String sessionId);
}
