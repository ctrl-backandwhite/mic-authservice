package com.backandwhite.infrastructure.db.postgres.repository.impl;

import static com.backandwhite.provider.OauthClientProvider.oauthClient;
import static com.backandwhite.provider.OauthClientProvider.oauthClientEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backandwhite.domain.model.OauthClient;
import com.backandwhite.infrastructure.db.postgres.entity.OauthClientEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.OauthClientEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.OauthClientJpaRepositoryAdapter;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OauthClientRepositoryImplTest {

    @Mock
    private OauthClientEntityMapper oauthClientEntityMapper;

    @Mock
    private OauthClientJpaRepositoryAdapter oauthClientJpaRepositoryAdapter;

    @InjectMocks
    private OauthClientRepositoryImpl oauthClientRepository;

    @Test
    void save_mapsAndPersistsEntity() {
        OauthClient model = oauthClient();
        OauthClientEntity entity = oauthClientEntity();
        OauthClientEntity savedEntity = oauthClientEntity().withId(10L);
        OauthClient saved = oauthClient().withId(10L);

        when(oauthClientEntityMapper.toEntity(model)).thenReturn(entity);
        when(oauthClientJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(oauthClientEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        OauthClient result = oauthClientRepository.save(model);

        assertSame(saved, result);
        verify(oauthClientEntityMapper).toEntity(model);
        verify(oauthClientJpaRepositoryAdapter).save(entity);
        verify(oauthClientEntityMapper).toDomain(savedEntity);
    }

    @Test
    void findAll_mapsEntitiesToDomain() {
        List<OauthClientEntity> entities = List.of(oauthClientEntity());
        List<OauthClient> clients = List.of(oauthClient());

        when(oauthClientJpaRepositoryAdapter.findAll()).thenReturn(entities);
        when(oauthClientEntityMapper.toDomainList(entities)).thenReturn(clients);

        List<OauthClient> result = oauthClientRepository.findAll();

        assertSame(clients, result);
        verify(oauthClientJpaRepositoryAdapter).findAll();
        verify(oauthClientEntityMapper).toDomainList(entities);
    }

    @Test
    void update_delegatesToSaveFlow() {
        OauthClient model = oauthClient();
        OauthClientEntity entity = oauthClientEntity();
        OauthClientEntity savedEntity = oauthClientEntity().withId(99L);
        OauthClient saved = oauthClient().withId(99L);

        when(oauthClientEntityMapper.toEntity(model)).thenReturn(entity);
        when(oauthClientJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(oauthClientEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        OauthClient result = oauthClientRepository.update(model);

        assertSame(saved, result);
        verify(oauthClientEntityMapper).toEntity(model);
        verify(oauthClientJpaRepositoryAdapter).save(entity);
        verify(oauthClientEntityMapper).toDomain(savedEntity);
    }

    @Test
    void delete_delegatesToJpaAdapter() {
        oauthClientRepository.delete(42L);

        verify(oauthClientJpaRepositoryAdapter).deleteById(42L);
    }

    @Test
    void getById_existingEntity_returnsDomain() {
        OauthClientEntity entity = oauthClientEntity().withId(5L);
        OauthClient model = oauthClient().withId(5L);

        when(oauthClientJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.of(entity));
        when(oauthClientEntityMapper.toDomain(entity)).thenReturn(model);

        OauthClient result = oauthClientRepository.getById(5L);

        assertSame(model, result);
        verify(oauthClientJpaRepositoryAdapter).findById(5L);
        verify(oauthClientEntityMapper).toDomain(entity);
    }

    @Test
    void getById_missingEntity_returnsNull() {
        when(oauthClientJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.empty());

        OauthClient result = oauthClientRepository.getById(5L);

        assertThat(result).isNull();
        verify(oauthClientJpaRepositoryAdapter).findById(5L);
    }
}
