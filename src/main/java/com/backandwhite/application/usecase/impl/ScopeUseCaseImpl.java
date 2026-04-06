package com.backandwhite.application.usecase.impl;

import com.backandwhite.application.mapper.ScopeUpdateMapper;
import com.backandwhite.application.usecase.ScopeUseCase;
import com.backandwhite.domain.model.Scope;
import com.backandwhite.domain.repository.ScopeRepository;

import com.backandwhite.application.handler.ScopeCommandHandler;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;

@Log4j2
@Service
@AllArgsConstructor
public class ScopeUseCaseImpl implements ScopeUseCase {

    private final ScopeRepository scopeRepository;
    private final ScopeCommandHandler scopeCommandHandler;
    private final ScopeUpdateMapper scopeUpdateMapper;

    @Override
    @Transactional
    @CacheEvict(value = "scope_all", allEntries = true)
    public Scope save(Scope model) {
        log.debug("::> Creating scope {}", model);
        scopeCommandHandler.validate(model);
        return scopeRepository.save(model);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "scope_all")
    public List<Scope> findAll() {
        log.debug("::> Getting all scopes");
        return scopeRepository.findAll();
    }

    @Override
    @Cacheable(value = "scope", key = "#id")
    public Scope getById(Long id) {
        log.debug("::> Getting scope with id {}", id);
        Scope model = scopeRepository.getById(id);
        if (Objects.isNull(model)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("Scope", id);
        }
        return model;
    }

    @Override
    @Transactional
    @CachePut(value = "scope", key = "#id") // actualiza cache individual
    @CacheEvict(value = "scope_all", allEntries = true) // limpia cache de lista
    public Scope update(Scope model, Long id) {
        log.debug("::> Updating scope {}", model);
        Scope existing = this.getById(id);
        scopeUpdateMapper.updateFromModel(model, existing);
        return scopeRepository.update(existing);
    }

    @Override
    @Transactional
    @CacheEvict(value = { "scope_all", "scope" }, allEntries = true)
    public void delete(Long id) {
        Scope model = scopeRepository.getById(id);
        if (Objects.isNull(model)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("Scope", id);
        }
        log.debug("::> Deleting scope with id {}", id);
        scopeRepository.delete(id);
    }
}
