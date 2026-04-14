package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.repository.RoleRepository;
import com.backandwhite.infrastructure.db.postgres.entity.RoleEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.RoleEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.RoleJpaRepositoryAdapter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

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
        return entity != null ? roleEntityMapper.toDomain(entity) : null;
    }
}
