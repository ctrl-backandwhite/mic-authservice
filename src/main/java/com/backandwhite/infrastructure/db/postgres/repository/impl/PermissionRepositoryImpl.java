package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.Permission;
import com.backandwhite.domain.repository.PermissionRepository;
import com.backandwhite.infrastructure.db.postgres.entity.PermissionEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.PermissionEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.PermissionJpaRepositoryAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j2
@Repository
@AllArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {

    private final PermissionEntityMapper permissionEntityMapper;
    private final PermissionJpaRepositoryAdapter permissionJpaRepositoryAdapter;

    @Override
    public Permission save(Permission model) {
        PermissionEntity entity = permissionJpaRepositoryAdapter.save(permissionEntityMapper.toEntity(model));
        return permissionEntityMapper.toDomain(entity);
    }

    @Override
    public List<Permission> findAll() {
        List<PermissionEntity> entities = permissionJpaRepositoryAdapter.findAll();
        return permissionEntityMapper.toDomainList(entities);
    }

    @Override
    public Permission update(Permission model) {
        return this.save(model);
    }

    @Override
    public void delete(Long id) {
        permissionJpaRepositoryAdapter.deleteById(id);
    }

    @Override
    public Permission getById(Long id) {
        PermissionEntity entity = permissionJpaRepositoryAdapter.findById(id).orElse(null);
        return entity != null ? permissionEntityMapper.toDomain(entity) : null;
    }
}
