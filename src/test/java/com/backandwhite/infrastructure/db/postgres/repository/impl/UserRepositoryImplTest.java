package com.backandwhite.infrastructure.db.postgres.repository.impl;

import static com.backandwhite.provider.UserProvider.user;
import static com.backandwhite.provider.UserProvider.userEntity;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backandwhite.common.exception.EntityNotFoundException;
import com.backandwhite.domain.model.User;
import com.backandwhite.infrastructure.db.postgres.entity.UserEntity;
import com.backandwhite.infrastructure.db.postgres.mapper.UserEntityMapper;
import com.backandwhite.infrastructure.db.postgres.repository.UserJpaRepositoryAdapter;
import com.backandwhite.provider.UserProvider;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private UserEntityMapper userEntityMapper;

    @Mock
    private UserJpaRepositoryAdapter userJpaRepositoryAdapter;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    @Test
    void save_mapsAndPersistsEntity() {
        User model = user();
        UserEntity entity = userEntity();
        UserEntity savedEntity = userEntity().withId(10L);
        User saved = user().withId(10L);

        when(userEntityMapper.toEntity(model)).thenReturn(entity);
        when(userJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(userEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        User result = userRepository.save(model);

        assertSame(saved, result);
        verify(userEntityMapper).toEntity(model);
        verify(userJpaRepositoryAdapter).save(entity);
        verify(userEntityMapper).toDomain(savedEntity);
    }

    @Test
    void findAll_mapsEntitiesToDomain() {
        List<UserEntity> entities = List.of(userEntity());
        List<User> users = List.of(user());

        when(userJpaRepositoryAdapter.findAll()).thenReturn(entities);
        when(userEntityMapper.toDomainList(entities)).thenReturn(users);

        List<User> result = userRepository.findAll();

        assertSame(users, result);
        verify(userJpaRepositoryAdapter).findAll();
        verify(userEntityMapper).toDomainList(entities);
    }

    @Test
    void update_delegatesToSaveFlow() {
        User model = user();
        UserEntity entity = userEntity();
        UserEntity savedEntity = userEntity().withId(99L);
        User saved = user().withId(99L);

        when(userEntityMapper.toEntity(model)).thenReturn(entity);
        when(userJpaRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(userEntityMapper.toDomain(savedEntity)).thenReturn(saved);

        User result = userRepository.update(model);

        assertSame(saved, result);
        verify(userEntityMapper).toEntity(model);
        verify(userJpaRepositoryAdapter).save(entity);
        verify(userEntityMapper).toDomain(savedEntity);
    }

    @Test
    void delete_delegatesToJpaAdapter() {
        userRepository.delete(42L);

        verify(userJpaRepositoryAdapter).deleteById(42L);
    }

    @Test
    void getById_existingEntity_returnsDomain() {
        UserEntity entity = userEntity().withId(5L);
        User model = user().withId(5L);

        when(userJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.of(entity));
        when(userEntityMapper.toDomain(entity)).thenReturn(model);

        User result = userRepository.getById(5L);

        assertSame(model, result);
        verify(userJpaRepositoryAdapter).findById(5L);
        verify(userEntityMapper).toDomain(entity);
    }

    @Test
    void getById_missingEntity_throwsEntityNotFound() {
        when(userJpaRepositoryAdapter.findById(5L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userRepository.getById(5L));
        verify(userJpaRepositoryAdapter).findById(5L);
    }

    @Test
    void findUserByEmail_mapsEntityToDomain() {
        UserEntity entity = userEntity();
        User model = user();

        when(userJpaRepositoryAdapter.findByEmail(UserProvider.USER_EMAIL)).thenReturn(entity);
        when(userEntityMapper.toDomain(entity)).thenReturn(model);

        User result = userRepository.findUserByEmail(UserProvider.USER_EMAIL);

        assertSame(model, result);
        verify(userJpaRepositoryAdapter).findByEmail(UserProvider.USER_EMAIL);
        verify(userEntityMapper).toDomain(entity);
    }

    @Test
    void deleteAll_delegatesToJpaAdapter() {
        List<Long> ids = List.of(1L, 2L, 3L);

        userRepository.deleteAll(ids);

        verify(userJpaRepositoryAdapter).deleteAllByIdInBatch(ids);
    }

    @Test
    void findUserByNickName_mapsEntityToDomain() {
        UserEntity entity = userEntity();
        User model = user();

        when(userJpaRepositoryAdapter.findByNickName("johndoe")).thenReturn(entity);
        when(userEntityMapper.toDomain(entity)).thenReturn(model);

        User result = userRepository.findUserByNickName("johndoe");

        assertSame(model, result);
        verify(userJpaRepositoryAdapter).findByNickName("johndoe");
        verify(userEntityMapper).toDomain(entity);
    }

    @Test
    void findByActivationToken_mapsEntityToDomain() {
        UserEntity entity = userEntity();
        User model = user();

        when(userJpaRepositoryAdapter.findByActivationToken("token-123")).thenReturn(entity);
        when(userEntityMapper.toDomain(entity)).thenReturn(model);

        User result = userRepository.findByActivationToken("token-123");

        assertSame(model, result);
        verify(userJpaRepositoryAdapter).findByActivationToken("token-123");
        verify(userEntityMapper).toDomain(entity);
    }

    @Test
    void findByPasswordResetToken_mapsEntityToDomain() {
        UserEntity entity = userEntity();
        User model = user();

        when(userJpaRepositoryAdapter.findByPasswordResetToken("reset-token")).thenReturn(entity);
        when(userEntityMapper.toDomain(entity)).thenReturn(model);

        User result = userRepository.findByPasswordResetToken("reset-token");

        assertSame(model, result);
        verify(userJpaRepositoryAdapter).findByPasswordResetToken("reset-token");
        verify(userEntityMapper).toDomain(entity);
    }

    @Test
    void findByPasswordChangeCode_mapsEntityToDomain() {
        UserEntity entity = userEntity();
        User model = user();

        when(userJpaRepositoryAdapter.findByPasswordChangeCode("change-code")).thenReturn(entity);
        when(userEntityMapper.toDomain(entity)).thenReturn(model);

        User result = userRepository.findByPasswordChangeCode("change-code");

        assertSame(model, result);
        verify(userJpaRepositoryAdapter).findByPasswordChangeCode("change-code");
        verify(userEntityMapper).toDomain(entity);
    }

    @Test
    void findByRoleId_mapsEntitiesToDomain() {
        List<UserEntity> entities = List.of(userEntity());
        List<User> users = List.of(user());

        when(userJpaRepositoryAdapter.findByRolesId(1L)).thenReturn(entities);
        when(userEntityMapper.toDomainList(entities)).thenReturn(users);

        List<User> result = userRepository.findByRoleId(1L);

        assertSame(users, result);
        verify(userJpaRepositoryAdapter).findByRolesId(1L);
        verify(userEntityMapper).toDomainList(entities);
    }

    @Test
    void findByGroupId_mapsEntitiesToDomain() {
        List<UserEntity> entities = List.of(userEntity());
        List<User> users = List.of(user());

        when(userJpaRepositoryAdapter.findByGroupsId(2L)).thenReturn(entities);
        when(userEntityMapper.toDomainList(entities)).thenReturn(users);

        List<User> result = userRepository.findByGroupId(2L);

        assertSame(users, result);
        verify(userJpaRepositoryAdapter).findByGroupsId(2L);
        verify(userEntityMapper).toDomainList(entities);
    }
}
