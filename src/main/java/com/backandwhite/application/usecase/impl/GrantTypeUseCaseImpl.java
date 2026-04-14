package com.backandwhite.application.usecase.impl;

import com.backandwhite.application.handler.GrantTypeCommandHandler;
import com.backandwhite.application.mapper.GrantTypeUpdateMapper;
import com.backandwhite.application.usecase.GrantTypeUseCase;
import com.backandwhite.domain.model.GrantType;
import com.backandwhite.domain.repository.GrantTypeRepository;
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
public class GrantTypeUseCaseImpl implements GrantTypeUseCase {

    private final GrantTypeRepository grantTypeRepository;
    private final GrantTypeCommandHandler grantTypeCommandHandler;
    private final GrantTypeUpdateMapper grantTypeUpdateMapper;

    @Override
    @Transactional
    @CacheEvict(value = "grantType_all", allEntries = true)
    public GrantType save(GrantType model) {
        log.debug("::> Creating granttype {}", model);
        grantTypeCommandHandler.validate(model);
        return grantTypeRepository.save(model);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "grantType_all")
    public List<GrantType> findAll() {
        log.debug("::> Getting all granttypes");
        return grantTypeRepository.findAll();
    }

    @Override
    @Cacheable(value = "grantType", key = "#id")
    public GrantType getById(Long id) {
        log.debug("::> Getting granttype with id {}", id);
        GrantType model = grantTypeRepository.getById(id);
        if (Objects.isNull(model)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("GrantType", id);
        }
        return model;
    }

    @Override
    @Transactional
    @CachePut(value = "grantType", key = "#id")
    @CacheEvict(value = "grantType_all", allEntries = true)
    public GrantType update(GrantType model, Long id) {
        log.debug("::> Updating granttype {}", model);
        GrantType existing = this.getById(id);
        grantTypeUpdateMapper.updateFromModel(model, existing);
        return grantTypeRepository.update(existing);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"grantType_all", "grantType"}, allEntries = true)
    public void delete(Long id) {
        this.getById(id);
        log.debug("::> Deleting granttype with id {}", id);
        grantTypeRepository.delete(id);
    }
}
