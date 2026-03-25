package com.backandwhite.infrastructure.db.postgres.repository.impl;

import com.backandwhite.domain.model.User;
import com.backandwhite.domain.repository.UserRepository;
import com.backandwhite.infrastructure.db.postgres.entity.UserEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.UserEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.UserJpaRepositoryAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;

@Log4j2
@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserEntityMapper userEntityMapper;
    private final UserJpaRepositoryAdapter userJpaRepositoryAdapter;

    @Override
    public User save(User model) {
        UserEntity entity = userJpaRepositoryAdapter.save(userEntityMapper.toEntity(model));
        return userEntityMapper.toDomain(entity);
    }

    @Override
    public List<User> findAll() {
        List<UserEntity> entities = userJpaRepositoryAdapter.findAll();
        return userEntityMapper.toDomainList(entities);
    }

    @Override
    public User update(User model) {
        return this.save(model);
    }

    @Override
    public void delete(Long id) {
        userJpaRepositoryAdapter.deleteById(id);
    }

    @Override
    public void deleteAll(List<Long> ids) {
        userJpaRepositoryAdapter.deleteAllByIdInBatch(ids);
    }

    @Override
    public User getById(Long id) {
        UserEntity entity = userJpaRepositoryAdapter.findById(id).orElse(null);
        if (Objects.isNull(entity)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("User", id);
        }
        return userEntityMapper.toDomain(entity);
    }

    @Override
    public User findUserByEmail(String email) {
        UserEntity userEntity = userJpaRepositoryAdapter.findByEmail(email);
        return userEntityMapper.toDomain(userEntity);
    }

    @Override
    public User findByActivationToken(String activationToken) {
        UserEntity entity = userJpaRepositoryAdapter.findByActivationToken(activationToken);
        return userEntityMapper.toDomain(entity);
    }

    @Override
    public User findByPasswordResetToken(String token) {
        UserEntity entity = userJpaRepositoryAdapter.findByPasswordResetToken(token);
        return userEntityMapper.toDomain(entity);
    }

    @Override
    public List<User> findByRoleId(Long roleId) {
        List<UserEntity> entities = userJpaRepositoryAdapter.findByRolesId(roleId);
        return userEntityMapper.toDomainList(entities);
    }

    @Override
    public List<User> findByGroupId(Long groupId) {
        List<UserEntity> entities = userJpaRepositoryAdapter.findByGroupsId(groupId);
        return userEntityMapper.toDomainList(entities);
    }

    @Override
    public List<User> findByScopeId(Long scopeId) {
        List<UserEntity> entities = userJpaRepositoryAdapter.findByScopesId(scopeId);
        return userEntityMapper.toDomainList(entities);
    }
}
