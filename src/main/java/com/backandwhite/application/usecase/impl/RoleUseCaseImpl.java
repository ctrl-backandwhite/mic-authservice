package com.backandwhite.application.usecase.impl;

import com.backandwhite.application.usecase.RoleUseCase;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.repository.RoleRepository;

import com.backandwhite.application.handler.RoleCommandHandler;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
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
public class RoleUseCaseImpl implements RoleUseCase {

private final RoleRepository roleRepository;
private final RoleCommandHandler roleCommandHandler;


    @Override
    @Transactional
    @CacheEvict(value = "role_all", allEntries = true)
    public Role save(Role model) {
        log.debug("::> Creating role {}", model);
        roleCommandHandler.validate(model);
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
    @CachePut(value = "role", key = "#id") // actualiza cache individual
    @CacheEvict(value = "role_all", allEntries = true) // limpia cache de lista
    public Role update(Role model, Long id) {
        log.debug("::> Updating role {}", model);
        Role existing = this.getById(id);
        BeanUtils.copyProperties(model, existing, "id");
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
