package com.backandwhite.application.usecase.impl;

import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;

import com.backandwhite.application.mapper.RedirectUriUpdateMapper;
import com.backandwhite.application.usecase.RedirectUriUseCase;
import com.backandwhite.domain.model.RedirectUri;
import com.backandwhite.domain.repository.RedirectUriRepository;
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
public class RedirectUriUseCaseImpl implements RedirectUriUseCase {

    private final RedirectUriRepository redirectUriRepository;
    private final RedirectUriUpdateMapper redirectUriUpdateMapper;

    @Override
    @Transactional
    @CacheEvict(value = "redirectUri_all", allEntries = true)
    public RedirectUri save(RedirectUri model) {
        log.debug("::> Creating redirecturi {}", model);
        return redirectUriRepository.save(model);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "redirectUri_all")
    public List<RedirectUri> findAll() {
        log.debug("::> Getting all redirecturis");
        return redirectUriRepository.findAll();
    }

    @Override
    @Cacheable(value = "redirectUri", key = "#id")
    public RedirectUri getById(Long id) {
        log.debug("::> Getting redirecturi with id {}", id);
        return getByIdInternal(id);
    }

    /**
     * Internal lookup used by other use-case methods. Bypasses {@code @Cacheable}
     * AOP (Sonar S6809) — self-invocation would skip the proxy anyway.
     */
    private RedirectUri getByIdInternal(Long id) {
        RedirectUri model = redirectUriRepository.getById(id);
        if (Objects.isNull(model)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("RedirectUri", id);
        }
        return model;
    }

    @Override
    @Transactional
    @CachePut(value = "redirectUri", key = "#id")
    @CacheEvict(value = "redirectUri_all", allEntries = true)
    public RedirectUri update(RedirectUri model, Long id) {
        log.debug("::> Updating redirecturi {}", model);
        RedirectUri existing = getByIdInternal(id);
        redirectUriUpdateMapper.updateFromModel(model, existing);
        return redirectUriRepository.update(existing);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"redirectUri_all", "redirectUri"}, allEntries = true)
    public void delete(Long id) {
        getByIdInternal(id);
        log.debug("::> Deleting redirecturi with id {}", id);
        redirectUriRepository.delete(id);
    }
}
