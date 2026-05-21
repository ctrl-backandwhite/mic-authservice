package com.backandwhite.application.usecase.impl;

import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;

import com.backandwhite.application.mapper.PermissionUpdateMapper;
import com.backandwhite.application.usecase.PermissionUseCase;
import com.backandwhite.domain.model.Permission;
import com.backandwhite.domain.repository.PermissionRepository;
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
public class PermissionUseCaseImpl implements PermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final PermissionUpdateMapper permissionUpdateMapper;

    @Override
    @Transactional
    @CacheEvict(value = "permission_all", allEntries = true)
    public Permission save(Permission model) {
        log.debug("::> Creating permission {}", model);
        return permissionRepository.save(model);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "permission_all")
    public List<Permission> findAll() {
        log.debug("::> Getting all permissions");
        return permissionRepository.findAll();
    }

    @Override
    @Cacheable(value = "permission", key = "#id")
    public Permission getById(Long id) {
        log.debug("::> Getting permission with id {}", id);
        return getByIdInternal(id);
    }

    /**
     * Internal lookup used by other use-case methods. Bypasses {@code @Cacheable}
     * AOP (Sonar S6809) — self-invocation would skip the proxy anyway.
     */
    private Permission getByIdInternal(Long id) {
        Permission model = permissionRepository.getById(id);
        if (Objects.isNull(model)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("Permission", id);
        }
        return model;
    }

    @Override
    @Transactional
    @CachePut(value = "permission", key = "#id")
    @CacheEvict(value = "permission_all", allEntries = true)
    public Permission update(Permission model, Long id) {
        log.debug("::> Updating permission {}", model);
        Permission existing = getByIdInternal(id);
        permissionUpdateMapper.updateFromModel(model, existing);
        return permissionRepository.update(existing);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"permission_all", "permission"}, allEntries = true)
    public void delete(Long id) {
        getByIdInternal(id);
        log.debug("::> Deleting permission with id {}", id);
        permissionRepository.delete(id);
    }
}
