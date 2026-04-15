package com.backandwhite.application.usecase.impl;

import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;

import com.backandwhite.application.mapper.RoleUpdateMapper;
import com.backandwhite.application.usecase.RoleUseCase;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.repository.RoleRepository;
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
public class RoleUseCaseImpl implements RoleUseCase {

    private final RoleRepository roleRepository;
    private final RoleUpdateMapper roleUpdateMapper;

    @Override
    @Transactional
    @CacheEvict(value = "role_all", allEntries = true)
    public Role save(Role model) {
        log.debug("::> Creating role {}", model);
        return roleRepository.save(model);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "role_all")
    public List<Role> findAll() {
        log.debug("::> Getting all roles");
        return roleRepository.findAll();
    }

    @Override
    @Cacheable(value = "role", key = "#id")
    public Role getById(Long id) {
        log.debug("::> Getting role with id {}", id);
        Role model = roleRepository.getById(id);
        if (Objects.isNull(model)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("Role", id);
        }
        return model;
    }

    @Override
    @Transactional
    @CachePut(value = "role", key = "#id")
    @CacheEvict(value = "role_all", allEntries = true)
    public Role update(Role model, Long id) {
        log.debug("::> Updating role {}", model);
        Role existing = this.getById(id);
        roleUpdateMapper.updateFromModel(model, existing);
        return roleRepository.update(existing);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"role_all", "role"}, allEntries = true)
    public void delete(Long id) {
        this.getById(id);
        log.debug("::> Deleting role with id {}", id);
        roleRepository.delete(id);
    }
}
