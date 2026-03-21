package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.RedirectUri;
import com.backandwhite.infrastructure.db.postgres.entity.RedirectUriEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.RedirectUriEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.RedirectUriJpaRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.backandwhite.provider.RedirectUriProvider.redirectUri;
import static com.backandwhite.provider.RedirectUriProvider.redirectUriEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedirectUriRepositoryImplTest {

    @Mock
    private RedirectUriEntityMapper redirectUriEntityMapper;

    @Mock
    private RedirectUriJpaRepositoryAdapter redirectUriJpaRepositoryAdapter;

    @InjectMocks
    private RedirectUriRepositoryImpl redirectUriRepository;

    @Test
    void save_mapsAndPersistsEntity() {
        RedirectUri model = redirectUri();
        RedirectUriEntity entity = redirectUriEntity();
        RedirectUriEntity savedEntity = redirectUriEntity().withId(10L);
        RedirectUri saved = redirectUri().withId(10L);

        when(redirectUriEntityMapper.toEntity(model)).thenReturn(entity);
        when(redirectUriJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(redirectUriEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        RedirectUri result = redirectUriRepository.save(model);

        assertSame(saved, result);
        verify(redirectUriEntityMapper).toEntity(model);
        verify(redirectUriJpaRepositoryAdapter).save(entity);
        verify(redirectUriEntityMapper).toDomain(savedEntity);
    }

    @Test
    void findAll_mapsEntitiesToDomain() {
        List<RedirectUriEntity> entities = List.of(redirectUriEntity());
        List<RedirectUri> redirectUris = List.of(redirectUri());

        when(redirectUriJpaRepositoryAdapter.findAll()).thenReturn(entities);
        when(redirectUriEntityMapper.toDomainList(entities)).thenReturn(redirectUris);

        List<RedirectUri> result = redirectUriRepository.findAll();

        assertSame(redirectUris, result);
        verify(redirectUriJpaRepositoryAdapter).findAll();
        verify(redirectUriEntityMapper).toDomainList(entities);
    }

    @Test
    void update_delegatesToSaveFlow() {
        RedirectUri model = redirectUri();
        RedirectUriEntity entity = redirectUriEntity();
        RedirectUriEntity savedEntity = redirectUriEntity().withId(99L);
        RedirectUri saved = redirectUri().withId(99L);

        when(redirectUriEntityMapper.toEntity(model)).thenReturn(entity);
        when(redirectUriJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(redirectUriEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        RedirectUri result = redirectUriRepository.update(model);

        assertSame(saved, result);
        verify(redirectUriEntityMapper).toEntity(model);
        verify(redirectUriJpaRepositoryAdapter).save(entity);
        verify(redirectUriEntityMapper).toDomain(savedEntity);
    }

    @Test
    void delete_delegatesToJpaAdapter() {
        redirectUriRepository.delete(42L);

        verify(redirectUriJpaRepositoryAdapter).deleteById(42L);
    }

    @Test
    void getById_existingEntity_returnsDomain() {
        RedirectUriEntity entity = redirectUriEntity().withId(5L);
        RedirectUri model = redirectUri().withId(5L);

        when(redirectUriJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.of(entity));
        when(redirectUriEntityMapper.toDomain(entity)).thenReturn(model);

        RedirectUri result = redirectUriRepository.getById(5L);

        assertSame(model, result);
        verify(redirectUriJpaRepositoryAdapter).findById(5L);
        verify(redirectUriEntityMapper).toDomain(entity);
    }

    @Test
    void getById_missingEntity_throwsException() {
        when(redirectUriJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> redirectUriRepository.getById(5L))
                .isInstanceOf(com.backandwhite.common.exception.EntityNotFoundException.class);
        verify(redirectUriJpaRepositoryAdapter).findById(5L);
    }
}
