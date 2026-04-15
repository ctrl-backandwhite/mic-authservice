package com.backandwhite.infrastructure.db.postgres.repository.impl;

import static com.backandwhite.provider.PermissionProvider.permission;
import static com.backandwhite.provider.PermissionProvider.permissionEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backandwhite.domain.model.Permission;
import com.backandwhite.infrastructure.db.postgres.entity.PermissionEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.PermissionEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.PermissionJpaRepositoryAdapter;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PermissionRepositoryImplTest {

    @Mock
    private PermissionEntityMapper permissionEntityMapper;

    @Mock
    private PermissionJpaRepositoryAdapter permissionJpaRepositoryAdapter;

    @InjectMocks
    private PermissionRepositoryImpl permissionRepository;

    @Test
    void save_mapsAndPersistsEntity() {
        Permission model = permission();
        PermissionEntity entity = permissionEntity();
        PermissionEntity savedEntity = permissionEntity().withId(10L);
        Permission saved = permission().withId(10L);

        when(permissionEntityMapper.toEntity(model)).thenReturn(entity);
        when(permissionJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(permissionEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        Permission result = permissionRepository.save(model);

        assertSame(saved, result);
        verify(permissionEntityMapper).toEntity(model);
        verify(permissionJpaRepositoryAdapter).save(entity);
        verify(permissionEntityMapper).toDomain(savedEntity);
    }

    @Test
    void findAll_mapsEntitiesToDomain() {
        List<PermissionEntity> entities = List.of(permissionEntity());
        List<Permission> permissions = List.of(permission());

        when(permissionJpaRepositoryAdapter.findAll()).thenReturn(entities);
        when(permissionEntityMapper.toDomainList(entities)).thenReturn(permissions);

        List<Permission> result = permissionRepository.findAll();

        assertSame(permissions, result);
        verify(permissionJpaRepositoryAdapter).findAll();
        verify(permissionEntityMapper).toDomainList(entities);
    }

    @Test
    void update_delegatesToSaveFlow() {
        Permission model = permission();
        PermissionEntity entity = permissionEntity();
        PermissionEntity savedEntity = permissionEntity().withId(99L);
        Permission saved = permission().withId(99L);

        when(permissionEntityMapper.toEntity(model)).thenReturn(entity);
        when(permissionJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(permissionEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        Permission result = permissionRepository.update(model);

        assertSame(saved, result);
    }

    @Test
    void delete_delegatesToJpaAdapter() {
        permissionRepository.delete(42L);

        verify(permissionJpaRepositoryAdapter).deleteById(42L);
    }

    @Test
    void getById_existingEntity_returnsDomain() {
        PermissionEntity entity = permissionEntity().withId(5L);
        Permission model = permission().withId(5L);

        when(permissionJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.of(entity));
        when(permissionEntityMapper.toDomain(entity)).thenReturn(model);

        Permission result = permissionRepository.getById(5L);

        assertSame(model, result);
        verify(permissionJpaRepositoryAdapter).findById(5L);
        verify(permissionEntityMapper).toDomain(entity);
    }

    @Test
    void getById_missingEntity_returnsNull() {
        when(permissionJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.empty());

        Permission result = permissionRepository.getById(5L);

        assertThat(result).isNull();
        verify(permissionJpaRepositoryAdapter).findById(5L);
    }
}
