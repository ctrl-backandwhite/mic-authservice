package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.Scope;
import com.backandwhite.domain.repository.ScopeRepository;
import com.backandwhite.infrastructure.db.postgres.entity.ScopeEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.ScopeEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.ScopeJpaRepositoryAdapter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
@AllArgsConstructor
public class ScopeRepositoryImpl implements ScopeRepository {

    private final ScopeEntityMapper scopeEntityMapper;
    private final ScopeJpaRepositoryAdapter scopeJpaRepositoryAdapter;

    @Override
    public Scope save(Scope model) {
        ScopeEntity entity = scopeJpaRepositoryAdapter.save(scopeEntityMapper.toEntity(model));
        return scopeEntityMapper.toDomain(entity);
    }

    @Override
    public List<Scope> findAll() {
        List<ScopeEntity> entities = scopeJpaRepositoryAdapter.findAll();
        return scopeEntityMapper.toDomainList(entities);
    }

    @Override
    public Scope update(Scope model) {
        return this.save(model);
    }

    @Override
    public void delete(Long id) {
        scopeJpaRepositoryAdapter.deleteById(id);
    }

    @Override
    public Scope getById(Long id) {
        ScopeEntity entity = scopeJpaRepositoryAdapter.findById(id).orElse(null);
        return entity != null ? scopeEntityMapper.toDomain(entity) : null;
    }
}
