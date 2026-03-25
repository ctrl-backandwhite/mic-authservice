package com.backandwhite.application.usecase.impl;

import com.backandwhite.application.handler.RoleCommandHandler;
import com.backandwhite.common.exception.EntityNotFoundException;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.backandwhite.provider.RoleProvider.adminRole;
import static com.backandwhite.provider.RoleProvider.userRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleUseCaseImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleCommandHandler roleCommandHandler;

    @InjectMocks
    private RoleUseCaseImpl roleUseCase;

    @Test
    void save_validRole_delegatesToHandlerAndRepository() {
        Role input = adminRole().withId(null);
        Role saved = adminRole();

        when(roleRepository.save(input)).thenReturn(saved);

        Role result = roleUseCase.save(input);

        assertSame(saved, result);
        verify(roleCommandHandler).validate(input);
        verify(roleRepository).save(input);
    }

    @Test
    void findAll_returnsRepositoryList() {
        List<Role> roles = List.of(
                adminRole(),
                userRole());

        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleUseCase.findAll();

        assertSame(roles, result);
        verify(roleRepository).findAll();
    }

    @Test
    void getById_existingRole_returnsRole() {
        Role role = adminRole().withId(5L);

        when(roleRepository.getById(5L)).thenReturn(role);

        Role result = roleUseCase.getById(5L);

        assertSame(role, result);
        verify(roleRepository).getById(5L);
    }

    @Test
    void getById_missingRole_throwsEntityNotFound() {
        when(roleRepository.getById(10L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> roleUseCase.getById(10L));
        verify(roleRepository).getById(10L);
    }

    @Test
    void update_existingRole_copiesFieldsAndPersists() {
        Role existing = adminRole().withId(10L);
        Role update = userRole().withId(99L);

        when(roleRepository.getById(10L)).thenReturn(existing);
        when(roleRepository.update(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Role result = roleUseCase.update(update, 10L);

        verify(roleRepository).update(any(Role.class));
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(userRole().withId(10L));
    }

    @Test
    void delete_existingRole_delegatesToRepository() {
        Role existing = userRole().withId(7L);

        when(roleRepository.getById(7L)).thenReturn(existing);

        roleUseCase.delete(7L);

        verify(roleRepository).delete(7L);
    }

}
