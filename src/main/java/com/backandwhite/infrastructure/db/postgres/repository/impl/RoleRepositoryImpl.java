package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.repository.RoleRepository;
import com.backandwhite.infrastructure.db.postgres.entity.RoleEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.RoleEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.RoleJpaRepositoryAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;

@Log4j2
@Repository
@AllArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleEntityMapper roleEntityMapper;
    private final RoleJpaRepositoryAdapter roleJpaRepositoryAdapter;

    @Override
    public Role save(Role model) {
        RoleEntity entity = roleJpaRepositoryAdapter.save(roleEntityMapper.toEntity(model));
        return roleEntityMapper.toDomain(entity);
    }

    @Override
    public List<Role> findAll() {
        List<RoleEntity> entities = roleJpaRepositoryAdapter.findAll();
        return roleEntityMapper.toDomainList(entities);
    }

    @Override
    public Role update(Role model) {
        return this.save(model);
    }

    @Override
    public void delete(Long id) {
        roleJpaRepositoryAdapter.deleteById(id);
    }

    @Override
    public Role getById(Long id) {
        RoleEntity entity = roleJpaRepositoryAdapter.findById(id).orElse(null);
        if (Objects.isNull(entity)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("Role", id);
        }
        return roleEntityMapper.toDomain(entity);
    }
}
