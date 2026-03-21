package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.GrantType;
import com.backandwhite.domain.repository.GrantTypeRepository;
import com.backandwhite.infrastructure.db.postgres.entity.GrantTypeEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.GrantTypeEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.GrantTypeJpaRepositoryAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;

@Log4j2
@Repository
@AllArgsConstructor
public class GrantTypeRepositoryImpl implements GrantTypeRepository {

    private final GrantTypeEntityMapper grantTypeEntityMapper;
    private final GrantTypeJpaRepositoryAdapter grantTypeJpaRepositoryAdapter;

    @Override
    public GrantType save(GrantType model) {
        GrantTypeEntity entity = grantTypeJpaRepositoryAdapter.save(grantTypeEntityMapper.toEntity(model));
        return grantTypeEntityMapper.toDomain(entity);
    }

    @Override
    public List<GrantType> findAll() {
        List<GrantTypeEntity> entities = grantTypeJpaRepositoryAdapter.findAll();
        return grantTypeEntityMapper.toDomainList(entities);
    }

    @Override
    public GrantType update(GrantType model) {
        return this.save(model);
    }

    @Override
    public void delete(Long id) {
        grantTypeJpaRepositoryAdapter.deleteById(id);
    }

    @Override
    public GrantType getById(Long id) {
        GrantTypeEntity entity = grantTypeJpaRepositoryAdapter.findById(id).orElse(null);
        if (Objects.isNull(entity)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("GrantType", id);
        }
        return grantTypeEntityMapper.toDomain(entity);
    }
}
