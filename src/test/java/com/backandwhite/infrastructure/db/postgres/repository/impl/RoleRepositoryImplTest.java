package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.Role;
import com.backandwhite.infrastructure.db.postgres.entity.RoleEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.RoleEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.RoleJpaRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.backandwhite.provider.RoleProvider.role;
import static com.backandwhite.provider.RoleProvider.roleEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleRepositoryImplTest {

    @Mock
    private RoleEntityMapper roleEntityMapper;

    @Mock
    private RoleJpaRepositoryAdapter roleJpaRepositoryAdapter;

    @InjectMocks
    private RoleRepositoryImpl roleRepository;

    @Test
    void save_mapsAndPersistsEntity() {
        Role model = role();
        RoleEntity entity = roleEntity();
        RoleEntity savedEntity = roleEntity().withId(10L);
        Role saved = role().withId(10L);

        when(roleEntityMapper.toEntity(model)).thenReturn(entity);
        when(roleJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(roleEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        Role result = roleRepository.save(model);

        assertSame(saved, result);
        verify(roleEntityMapper).toEntity(model);
        verify(roleJpaRepositoryAdapter).save(entity);
        verify(roleEntityMapper).toDomain(savedEntity);
    }

    @Test
    void findAll_mapsEntitiesToDomain() {
        List<RoleEntity> entities = List.of(roleEntity());
        List<Role> roles = List.of(role());

        when(roleJpaRepositoryAdapter.findAll()).thenReturn(entities);
        when(roleEntityMapper.toDomainList(entities)).thenReturn(roles);

        List<Role> result = roleRepository.findAll();

        assertSame(roles, result);
        verify(roleJpaRepositoryAdapter).findAll();
        verify(roleEntityMapper).toDomainList(entities);
    }

    @Test
    void update_delegatesToSaveFlow() {
        Role model = role();
        RoleEntity entity = roleEntity();
        RoleEntity savedEntity = roleEntity().withId(99L);
        Role saved = role().withId(99L);

        when(roleEntityMapper.toEntity(model)).thenReturn(entity);
        when(roleJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(roleEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        Role result = roleRepository.update(model);

        assertSame(saved, result);
        verify(roleEntityMapper).toEntity(model);
        verify(roleJpaRepositoryAdapter).save(entity);
        verify(roleEntityMapper).toDomain(savedEntity);
    }

    @Test
    void delete_delegatesToJpaAdapter() {
        roleRepository.delete(42L);

        verify(roleJpaRepositoryAdapter).deleteById(42L);
    }

    @Test
    void getById_existingEntity_returnsDomain() {
        RoleEntity entity = roleEntity().withId(5L);
        Role model = role().withId(5L);

        when(roleJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.of(entity));
        when(roleEntityMapper.toDomain(entity)).thenReturn(model);

        Role result = roleRepository.getById(5L);

        assertSame(model, result);
        verify(roleJpaRepositoryAdapter).findById(5L);
        verify(roleEntityMapper).toDomain(entity);
    }

    @Test
    void getById_missingEntity_throwsException() {
        when(roleJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleRepository.getById(5L))
                .isInstanceOf(com.backandwhite.common.exception.EntityNotFoundException.class);
        verify(roleJpaRepositoryAdapter).findById(5L);
    }
}
