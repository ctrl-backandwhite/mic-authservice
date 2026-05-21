package com.backandwhite.application.usecase.impl;

import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;

import com.backandwhite.application.handler.OauthClientCommandHandler;
import com.backandwhite.application.mapper.OauthClientUpdateMapper;
import com.backandwhite.application.usecase.OauthClientUseCase;
import com.backandwhite.domain.model.OauthClient;
import com.backandwhite.domain.repository.OauthClientRepository;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@AllArgsConstructor
public class OauthClientUseCaseImpl implements OauthClientUseCase {

    private final OauthClientRepository oauthClientRepository;
    private final OauthClientCommandHandler oauthClientCommandHandler;
    private final OauthClientUpdateMapper oauthClientUpdateMapper;

    @Override
    @Transactional
    @CacheEvict(value = "oauthClient_all", allEntries = true)
    public OauthClient save(OauthClient model) {
        log.debug("::> Creating oauthclient {}", model);
        oauthClientCommandHandler.validate(model);
        return oauthClientRepository.save(model);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "oauthClient_all")
    public List<OauthClient> findAll() {
        log.debug("::> Getting all oauthclients");
        return oauthClientRepository.findAll();
    }

    @Override
    @Cacheable(value = "oauthClient", key = "#id")
    public OauthClient getById(Long id) {
        log.debug("::> Getting oauthclient with id {}", id);
        return getByIdInternal(id);
    }

    /**
     * Internal lookup used by other use-case methods. Bypasses {@code @Cacheable}
     * AOP (Sonar S6809) — self-invocation would skip the proxy anyway.
     */
    private OauthClient getByIdInternal(Long id) {
        OauthClient model = oauthClientRepository.getById(id);
        if (Objects.isNull(model)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("OauthClient", id);
        }
        return model;
    }

    @Override
    @Transactional
    @CachePut(value = "oauthClient", key = "#id")
    @CacheEvict(value = "oauthClient_all", allEntries = true)
    public OauthClient update(OauthClient model, Long id) {
        log.debug("::> Updating oauthclient {}", model);
        OauthClient existing = getByIdInternal(id);
        oauthClientUpdateMapper.updateFromModel(model, existing);
        oauthClientCommandHandler.validate(existing);
        return oauthClientRepository.update(existing);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"oauthClient_all", "oauthClient"}, allEntries = true)
    public void delete(Long id) {
        getByIdInternal(id);
        log.debug("::> Deleting oauthclient with id {}", id);
        oauthClientRepository.delete(id);
    }
}
