package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.RedirectUri;
import com.backandwhite.domain.repository.RedirectUriRepository;
import com.backandwhite.infrastructure.db.postgres.entity.RedirectUriEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.RedirectUriEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.RedirectUriJpaRepositoryAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;

@Log4j2
@Repository
@AllArgsConstructor
public class RedirectUriRepositoryImpl implements RedirectUriRepository {

    private final RedirectUriEntityMapper redirectUriEntityMapper;
    private final RedirectUriJpaRepositoryAdapter redirectUriJpaRepositoryAdapter;

    @Override
    public RedirectUri save(RedirectUri model) {
        RedirectUriEntity entity = redirectUriJpaRepositoryAdapter.save(redirectUriEntityMapper.toEntity(model));
        return redirectUriEntityMapper.toDomain(entity);
    }

    @Override
    public List<RedirectUri> findAll() {
        List<RedirectUriEntity> entities = redirectUriJpaRepositoryAdapter.findAll();
        return redirectUriEntityMapper.toDomainList(entities);
    }

    @Override
    public RedirectUri update(RedirectUri model) {
        return this.save(model);
    }

    @Override
    public void delete(Long id) {
        redirectUriJpaRepositoryAdapter.deleteById(id);
    }

    @Override
    public RedirectUri getById(Long id) {
        RedirectUriEntity entity = redirectUriJpaRepositoryAdapter.findById(id).orElse(null);
        if (Objects.isNull(entity)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("RedirectUri", id);
        }
        return redirectUriEntityMapper.toDomain(entity);
    }
}
