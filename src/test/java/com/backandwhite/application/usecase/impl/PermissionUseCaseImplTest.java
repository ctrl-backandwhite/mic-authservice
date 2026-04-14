package com.backandwhite.application.usecase.impl;

import static com.backandwhite.provider.PermissionProvider.permission;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backandwhite.application.handler.PermissionCommandHandler;
import com.backandwhite.application.mapper.PermissionUpdateMapper;
import com.backandwhite.common.exception.EntityNotFoundException;
import com.backandwhite.domain.model.Permission;
import com.backandwhite.domain.repository.PermissionRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

@ExtendWith(MockitoExtension.class)
class PermissionUseCaseImplTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private PermissionCommandHandler permissionCommandHandler;

    @Mock
    private PermissionUpdateMapper permissionUpdateMapper;

    @InjectMocks
    private PermissionUseCaseImpl permissionUseCase;

    @Test
    void save_validPermission_delegatesToHandlerAndRepository() {
        Permission input = permission().withId(null);
        Permission saved = permission();

        when(permissionRepository.save(input)).thenReturn(saved);

        Permission result = permissionUseCase.save(input);

        assertSame(saved, result);
        verify(permissionCommandHandler).validate(input);
        verify(permissionRepository).save(input);
    }

    @Test
    void findAll_returnsRepositoryList() {
        List<Permission> permissions = List.of(permission());

        when(permissionRepository.findAll()).thenReturn(permissions);

        List<Permission> result = permissionUseCase.findAll();

        assertSame(permissions, result);
        verify(permissionRepository).findAll();
    }

    @Test
    void getById_existingPermission_returnsPermission() {
        Permission perm = permission().withId(5L);

        when(permissionRepository.getById(5L)).thenReturn(perm);

        Permission result = permissionUseCase.getById(5L);

        assertSame(perm, result);
        verify(permissionRepository).getById(5L);
    }

    @Test
    void getById_missingPermission_throwsEntityNotFound() {
        when(permissionRepository.getById(10L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> permissionUseCase.getById(10L));
        verify(permissionRepository).getById(10L);
    }

    @Test
    void update_existingPermission_copiesFieldsAndPersists() {
        Permission existing = permission().withId(10L);
        Permission update = permission().withId(99L).withName("Updated");

        when(permissionRepository.getById(10L)).thenReturn(existing);
        doAnswer(inv -> {
            BeanUtils.copyProperties(inv.getArgument(0), inv.getArgument(1), "id");
            return null;
        }).when(permissionUpdateMapper).updateFromModel(any(Permission.class), any(Permission.class));
        when(permissionRepository.update(any(Permission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Permission result = permissionUseCase.update(update, 10L);

        verify(permissionRepository).update(any(Permission.class));
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Updated");
    }

    @Test
    void delete_existingPermission_delegatesToRepository() {
        Permission existing = permission().withId(7L);

        when(permissionRepository.getById(7L)).thenReturn(existing);

        permissionUseCase.delete(7L);

        verify(permissionRepository).delete(7L);
    }
}
