package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.Scope;
import com.backandwhite.infrastructure.db.postgres.entity.ScopeEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.ScopeEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.ScopeJpaRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.backandwhite.provider.ScopeProvider.scope;
import static com.backandwhite.provider.ScopeProvider.scopeEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScopeRepositoryImplTest {

    @Mock
    private ScopeEntityMapper scopeEntityMapper;

    @Mock
    private ScopeJpaRepositoryAdapter scopeJpaRepositoryAdapter;

    @InjectMocks
    private ScopeRepositoryImpl scopeRepository;

    @Test
    void save_mapsAndPersistsEntity() {
        Scope model = scope();
        ScopeEntity entity = scopeEntity();
        ScopeEntity savedEntity = scopeEntity().withId(10L);
        Scope saved = scope().withId(10L);

        when(scopeEntityMapper.toEntity(model)).thenReturn(entity);
        when(scopeJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(scopeEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        Scope result = scopeRepository.save(model);

        assertSame(saved, result);
        verify(scopeEntityMapper).toEntity(model);
        verify(scopeJpaRepositoryAdapter).save(entity);
        verify(scopeEntityMapper).toDomain(savedEntity);
    }

    @Test
    void findAll_mapsEntitiesToDomain() {
        List<ScopeEntity> entities = List.of(scopeEntity());
        List<Scope> scopes = List.of(scope());

        when(scopeJpaRepositoryAdapter.findAll()).thenReturn(entities);
        when(scopeEntityMapper.toDomainList(entities)).thenReturn(scopes);

        List<Scope> result = scopeRepository.findAll();

        assertSame(scopes, result);
        verify(scopeJpaRepositoryAdapter).findAll();
        verify(scopeEntityMapper).toDomainList(entities);
    }

    @Test
    void update_delegatesToSaveFlow() {
        Scope model = scope();
        ScopeEntity entity = scopeEntity();
        ScopeEntity savedEntity = scopeEntity().withId(99L);
        Scope saved = scope().withId(99L);

        when(scopeEntityMapper.toEntity(model)).thenReturn(entity);
        when(scopeJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(scopeEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        Scope result = scopeRepository.update(model);

        assertSame(saved, result);
        verify(scopeEntityMapper).toEntity(model);
        verify(scopeJpaRepositoryAdapter).save(entity);
        verify(scopeEntityMapper).toDomain(savedEntity);
    }

    @Test
    void delete_delegatesToJpaAdapter() {
        scopeRepository.delete(42L);

        verify(scopeJpaRepositoryAdapter).deleteById(42L);
    }

    @Test
    void getById_existingEntity_returnsDomain() {
        ScopeEntity entity = scopeEntity().withId(5L);
        Scope model = scope().withId(5L);

        when(scopeJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.of(entity));
        when(scopeEntityMapper.toDomain(entity)).thenReturn(model);

        Scope result = scopeRepository.getById(5L);

        assertSame(model, result);
        verify(scopeJpaRepositoryAdapter).findById(5L);
        verify(scopeEntityMapper).toDomain(entity);
    }

    @Test
    void getById_missingEntity_throwsException() {
        when(scopeJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scopeRepository.getById(5L))
                .isInstanceOf(com.backandwhite.common.exception.EntityNotFoundException.class);
        verify(scopeJpaRepositoryAdapter).findById(5L);
    }
}
