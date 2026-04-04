package com.backandwhite.infrastructure.db.postgres.repository;

import com.backandwhite.infrastructure.db.postgres.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface UserSessionJpaRepositoryAdapter extends JpaRepository<UserSessionEntity, Long> {

    List<UserSessionEntity> findByUserIdAndRevokedFalseOrderByCreatedAtDesc(Long userId);

    UserSessionEntity findBySessionId(String sessionId);

    @Modifying
    @Query("UPDATE UserSessionEntity s SET s.revoked = true, s.revokedAt = :now WHERE s.sessionId = :sessionId")
    void revokeBySessionId(@Param("sessionId") String sessionId, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE UserSessionEntity s SET s.lastActiveAt = :now WHERE s.sessionId = :sessionId")
    void updateLastActiveAt(@Param("sessionId") String sessionId, @Param("now") Instant now);
}
