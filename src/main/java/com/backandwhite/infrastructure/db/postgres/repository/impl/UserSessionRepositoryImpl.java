package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.UserSession;
import com.backandwhite.domain.repository.UserSessionRepository;
import com.backandwhite.infrastructure.db.postgres.entity.UserSessionEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.UserSessionEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.UserSessionJpaRepositoryAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Log4j2
@Repository
@AllArgsConstructor
public class UserSessionRepositoryImpl implements UserSessionRepository {

    private final UserSessionEntityMapper mapper;
    private final UserSessionJpaRepositoryAdapter jpaAdapter;

    @Override
    public UserSession save(UserSession model) {
        UserSessionEntity entity = jpaAdapter.save(mapper.toEntity(model));
        return mapper.toDomain(entity);
    }

    @Override
    public List<UserSession> findActiveByUserId(Long userId) {
        List<UserSessionEntity> entities = jpaAdapter.findByUserIdAndRevokedFalseOrderByCreatedAtDesc(userId);
        return mapper.toDomainList(entities);
    }

    @Override
    public UserSession findBySessionId(String sessionId) {
        UserSessionEntity entity = jpaAdapter.findBySessionId(sessionId);
        return entity != null ? mapper.toDomain(entity) : null;
    }

    @Override
    @Transactional
    public void revokeSession(String sessionId) {
        jpaAdapter.revokeBySessionId(sessionId, Instant.now());
    }

    @Override
    @Transactional
    public void updateLastActiveAt(String sessionId) {
        jpaAdapter.updateLastActiveAt(sessionId, Instant.now());
    }
}
