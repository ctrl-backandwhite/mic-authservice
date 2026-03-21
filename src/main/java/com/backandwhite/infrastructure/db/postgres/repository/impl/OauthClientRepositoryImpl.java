package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.OauthClient;
import com.backandwhite.domain.repository.OauthClientRepository;
import com.backandwhite.infrastructure.db.postgres.entity.OauthClientEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.OauthClientEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.OauthClientJpaRepositoryAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;

@Log4j2
@Repository
@AllArgsConstructor
public class OauthClientRepositoryImpl implements OauthClientRepository {

    private final OauthClientEntityMapper oauthClientEntityMapper;
    private final OauthClientJpaRepositoryAdapter oauthClientJpaRepositoryAdapter;

    @Override
    public OauthClient save(OauthClient model) {
        OauthClientEntity entity = oauthClientJpaRepositoryAdapter.save(oauthClientEntityMapper.toEntity(model));
        return oauthClientEntityMapper.toDomain(entity);
    }

    @Override
    public List<OauthClient> findAll() {
        List<OauthClientEntity> entities = oauthClientJpaRepositoryAdapter.findAll();
        return oauthClientEntityMapper.toDomainList(entities);
    }

    @Override
    public OauthClient update(OauthClient model) {
        return this.save(model);
    }

    @Override
    public void delete(Long id) {
        oauthClientJpaRepositoryAdapter.deleteById(id);
    }

    @Override
    public OauthClient getById(Long id) {
        OauthClientEntity entity = oauthClientJpaRepositoryAdapter.findById(id).orElse(null);
        if (Objects.isNull(entity)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("OauthClient", id);
        }
        return oauthClientEntityMapper.toDomain(entity);
    }

    @Override
    public OauthClient findByClientId(String clientId) {
        OauthClientEntity entity = oauthClientJpaRepositoryAdapter.findByClientId(clientId);
        if (entity == null) {
            return null;
        }
        return oauthClientEntityMapper.toDomain(entity);
    }
}
