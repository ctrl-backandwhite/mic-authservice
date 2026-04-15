package com.backandwhite.infrastructure.db.postgres.repository.impl;

import static com.backandwhite.provider.UserSessionProvider.userSession;
import static com.backandwhite.provider.UserSessionProvider.userSessionEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backandwhite.domain.model.UserSession;
import com.backandwhite.infrastructure.db.postgres.entity.UserSessionEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.UserSessionEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.UserSessionJpaRepositoryAdapter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserSessionRepositoryImplTest {

    @Mock
    private UserSessionEntityMapper mapper;

    @Mock
    private UserSessionJpaRepositoryAdapter jpaAdapter;

    @InjectMocks
    private UserSessionRepositoryImpl userSessionRepository;

    @Test
    void save_mapsAndPersistsEntity() {
        UserSession model = userSession();
        UserSessionEntity entity = userSessionEntity();
        UserSessionEntity savedEntity = userSessionEntity();
        UserSession saved = userSession();

        when(mapper.toEntity(model)).thenReturn(entity);
        when(jpaAdapter.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(saved);

        UserSession result = userSessionRepository.save(model);

        assertSame(saved, result);
    }

    @Test
    void findActiveByUserId_returnsMappedList() {
        List<UserSessionEntity> entities = List.of(userSessionEntity());
        List<UserSession> sessions = List.of(userSession());

        when(jpaAdapter.findByUserIdAndRevokedFalseOrderByCreatedAtDesc(1L)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(sessions);

        List<UserSession> result = userSessionRepository.findActiveByUserId(1L);

        assertSame(sessions, result);
    }

    @Test
    void findBySessionId_existingSession_returnsDomain() {
        UserSessionEntity entity = userSessionEntity();
        UserSession session = userSession();

        when(jpaAdapter.findBySessionId("abc123session")).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(session);

        UserSession result = userSessionRepository.findBySessionId("abc123session");

        assertSame(session, result);
    }

    @Test
    void findBySessionId_missingSession_returnsNull() {
        when(jpaAdapter.findBySessionId("missing")).thenReturn(null);

        UserSession result = userSessionRepository.findBySessionId("missing");

        assertThat(result).isNull();
    }

    @Test
    void revokeSession_delegatesToJpaAdapter() {
        userSessionRepository.revokeSession("abc123session");

        verify(jpaAdapter).revokeBySessionId(ArgumentMatchers.eq("abc123session"), ArgumentMatchers.any());
    }

    @Test
    void updateLastActiveAt_delegatesToJpaAdapter() {
        userSessionRepository.updateLastActiveAt("abc123session");

        verify(jpaAdapter).updateLastActiveAt(ArgumentMatchers.eq("abc123session"), ArgumentMatchers.any());
    }
}
