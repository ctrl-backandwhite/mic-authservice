package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.GrantType;
import com.backandwhite.infrastructure.db.postgres.entity.GrantTypeEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.GrantTypeEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.GrantTypeJpaRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.backandwhite.provider.GrantTypeProvider.grantType;
import static com.backandwhite.provider.GrantTypeProvider.grantTypeEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantTypeRepositoryImplTest {

    @Mock
    private GrantTypeEntityMapper grantTypeEntityMapper;

    @Mock
    private GrantTypeJpaRepositoryAdapter grantTypeJpaRepositoryAdapter;

    @InjectMocks
    private GrantTypeRepositoryImpl grantTypeRepository;

    @Test
    void save_mapsAndPersistsEntity() {
        GrantType model = grantType();
        GrantTypeEntity entity = grantTypeEntity();
        GrantTypeEntity savedEntity = grantTypeEntity().withId(10L);
        GrantType saved = grantType().withId(10L);

        when(grantTypeEntityMapper.toEntity(model)).thenReturn(entity);
        when(grantTypeJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(grantTypeEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        GrantType result = grantTypeRepository.save(model);

        assertSame(saved, result);
        verify(grantTypeEntityMapper).toEntity(model);
        verify(grantTypeJpaRepositoryAdapter).save(entity);
        verify(grantTypeEntityMapper).toDomain(savedEntity);
    }

    @Test
    void findAll_mapsEntitiesToDomain() {
        List<GrantTypeEntity> entities = List.of(grantTypeEntity());
        List<GrantType> grantTypes = List.of(grantType());

        when(grantTypeJpaRepositoryAdapter.findAll()).thenReturn(entities);
        when(grantTypeEntityMapper.toDomainList(entities)).thenReturn(grantTypes);

        List<GrantType> result = grantTypeRepository.findAll();

        assertSame(grantTypes, result);
        verify(grantTypeJpaRepositoryAdapter).findAll();
        verify(grantTypeEntityMapper).toDomainList(entities);
    }

    @Test
    void update_delegatesToSaveFlow() {
        GrantType model = grantType();
        GrantTypeEntity entity = grantTypeEntity();
        GrantTypeEntity savedEntity = grantTypeEntity().withId(99L);
        GrantType saved = grantType().withId(99L);

        when(grantTypeEntityMapper.toEntity(model)).thenReturn(entity);
        when(grantTypeJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(grantTypeEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        GrantType result = grantTypeRepository.update(model);

        assertSame(saved, result);
        verify(grantTypeEntityMapper).toEntity(model);
        verify(grantTypeJpaRepositoryAdapter).save(entity);
        verify(grantTypeEntityMapper).toDomain(savedEntity);
    }

    @Test
    void delete_delegatesToJpaAdapter() {
        grantTypeRepository.delete(42L);

        verify(grantTypeJpaRepositoryAdapter).deleteById(42L);
    }

    @Test
    void getById_existingEntity_returnsDomain() {
        GrantTypeEntity entity = grantTypeEntity().withId(5L);
        GrantType model = grantType().withId(5L);

        when(grantTypeJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.of(entity));
        when(grantTypeEntityMapper.toDomain(entity)).thenReturn(model);

        GrantType result = grantTypeRepository.getById(5L);

        assertSame(model, result);
        verify(grantTypeJpaRepositoryAdapter).findById(5L);
        verify(grantTypeEntityMapper).toDomain(entity);
    }

    @Test
    void getById_missingEntity_returnsNull() {
        when(grantTypeJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.empty());
        when(grantTypeEntityMapper.toDomain(null)).thenReturn(null);

        GrantType result = grantTypeRepository.getById(5L);

        assertThat(result).isNull();
        verify(grantTypeJpaRepositoryAdapter).findById(5L);
        verify(grantTypeEntityMapper).toDomain(null);
    }
}
