package com.backandwhite.application.usecase.impl;

import com.backandwhite.application.handler.GroupCommandHandler;
import com.backandwhite.application.mapper.GroupUpdateMapper;
import com.backandwhite.application.usecase.GroupUseCase;
import com.backandwhite.domain.model.Group;
import com.backandwhite.domain.repository.GroupRepository;
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
public class GroupUseCaseImpl implements GroupUseCase {

    private final GroupRepository groupRepository;
    private final GroupCommandHandler groupCommandHandler;
    private final GroupUpdateMapper groupUpdateMapper;

    @Override
    @Transactional
    @CacheEvict(value = "group_all", allEntries = true)
    public Group save(Group model) {
        log.debug("::> Creating group {}", model);
        groupCommandHandler.validate(model);
        return groupRepository.save(model);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "group_all")
    public List<Group> findAll() {
        log.debug("::> Getting all groups");
        return groupRepository.findAll();
    }

    @Override
    @Cacheable(value = "group", key = "#id")
    public Group getById(Long id) {
        log.debug("::> Getting group with id {}", id);
        Group model = groupRepository.getById(id);
        if (Objects.isNull(model)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("Group", id);
        }
        return model;
    }

    @Override
    @Transactional
    @CachePut(value = "group", key = "#id")
    @CacheEvict(value = "group_all", allEntries = true)
    public Group update(Group model, Long id) {
        log.debug("::> Updating group {}", model);
        Group existing = this.getById(id);
        groupUpdateMapper.updateFromModel(model, existing);
        groupCommandHandler.validate(existing);
        return groupRepository.update(existing);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"group_all", "group"}, allEntries = true)
    public void delete(Long id) {
        this.getById(id);
        log.debug("::> Deleting group with id {}", id);
        groupRepository.delete(id);
    }
}
