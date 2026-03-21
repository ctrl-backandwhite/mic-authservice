package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.Group;
import com.backandwhite.infrastructure.db.postgres.entity.GroupEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.GroupEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.GroupJpaRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.backandwhite.provider.GroupProvider.group;
import static com.backandwhite.provider.GroupProvider.groupEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupRepositoryImplTest {

    @Mock
    private GroupEntityMapper groupEntityMapper;

    @Mock
    private GroupJpaRepositoryAdapter groupJpaRepositoryAdapter;

    @InjectMocks
    private GroupRepositoryImpl groupRepository;

    @Test
    void save_mapsAndPersistsEntity() {
        Group model = group();
        GroupEntity entity = groupEntity();
        GroupEntity savedEntity = groupEntity().withId(10L);
        Group saved = group().withId(10L);

        when(groupEntityMapper.toEntity(model)).thenReturn(entity);
        when(groupJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(groupEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        Group result = groupRepository.save(model);

        assertSame(saved, result);
        verify(groupEntityMapper).toEntity(model);
        verify(groupJpaRepositoryAdapter).save(entity);
        verify(groupEntityMapper).toDomain(savedEntity);
    }

    @Test
    void findAll_mapsEntitiesToDomain() {
        List<GroupEntity> entities = List.of(groupEntity());
        List<Group> groups = List.of(group());

        when(groupJpaRepositoryAdapter.findAll()).thenReturn(entities);
        when(groupEntityMapper.toDomainList(entities)).thenReturn(groups);

        List<Group> result = groupRepository.findAll();

        assertSame(groups, result);
        verify(groupJpaRepositoryAdapter).findAll();
        verify(groupEntityMapper).toDomainList(entities);
    }

    @Test
    void update_delegatesToSaveFlow() {
        Group model = group();
        GroupEntity entity = groupEntity();
        GroupEntity savedEntity = groupEntity().withId(99L);
        Group saved = group().withId(99L);

        when(groupEntityMapper.toEntity(model)).thenReturn(entity);
        when(groupJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(groupEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        Group result = groupRepository.update(model);

        assertSame(saved, result);
        verify(groupEntityMapper).toEntity(model);
        verify(groupJpaRepositoryAdapter).save(entity);
        verify(groupEntityMapper).toDomain(savedEntity);
    }

    @Test
    void delete_delegatesToJpaAdapter() {
        groupRepository.delete(42L);

        verify(groupJpaRepositoryAdapter).deleteById(42L);
    }

    @Test
    void getById_existingEntity_returnsDomain() {
        GroupEntity entity = groupEntity().withId(5L);
        Group model = group().withId(5L);

        when(groupJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.of(entity));
        when(groupEntityMapper.toDomain(entity)).thenReturn(model);

        Group result = groupRepository.getById(5L);

        assertSame(model, result);
        verify(groupJpaRepositoryAdapter).findById(5L);
        verify(groupEntityMapper).toDomain(entity);
    }

    @Test
    void getById_missingEntity_throwsException() {
        when(groupJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupRepository.getById(5L))
                .isInstanceOf(com.backandwhite.common.exception.EntityNotFoundException.class);
        verify(groupJpaRepositoryAdapter).findById(5L);
    }
}
