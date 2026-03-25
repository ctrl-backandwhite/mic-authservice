package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.Group;
import com.backandwhite.domain.repository.GroupRepository;
import com.backandwhite.infrastructure.db.postgres.entity.GroupEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.GroupEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.GroupJpaRepositoryAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;

@Log4j2
@Repository
@AllArgsConstructor
public class GroupRepositoryImpl implements GroupRepository {

    private final GroupEntityMapper groupEntityMapper;
    private final GroupJpaRepositoryAdapter groupJpaRepositoryAdapter;

    @Override
    public Group save(Group model) {
        GroupEntity entity = groupJpaRepositoryAdapter.save(groupEntityMapper.toEntity(model));
        return groupEntityMapper.toDomain(entity);
    }

    @Override
    public List<Group> findAll() {
        List<GroupEntity> entities = groupJpaRepositoryAdapter.findAll();
        return groupEntityMapper.toDomainList(entities);
    }

    @Override
    public Group update(Group model) {
        return this.save(model);
    }

    @Override
    public void delete(Long id) {
        groupJpaRepositoryAdapter.deleteById(id);
    }

    @Override
    public Group getById(Long id) {
        GroupEntity entity = groupJpaRepositoryAdapter.findById(id).orElse(null);
        ENTITY_NOT_FOUND.toEntityNotFound("Group", id);
        return groupEntityMapper.toDomain(entity);
    }
}
