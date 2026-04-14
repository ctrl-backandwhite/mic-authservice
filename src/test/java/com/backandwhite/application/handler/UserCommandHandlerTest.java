package com.backandwhite.application.handler;

import static com.backandwhite.provider.GroupProvider.adminGroup;
import static com.backandwhite.provider.RoleProvider.adminRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.backandwhite.domain.model.Group;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.repository.GroupRepository;
import com.backandwhite.domain.repository.RoleRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCommandHandlerTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private UserCommandHandler userCommandHandler;

    @Test
    void validate_withRoles_fetchesAndSetsRoles() {
        Role inputRole = Role.builder().id(1L).build();
        Role fetched = adminRole();
        User user = User.builder().roles(new ArrayList<>(List.of(inputRole))).groups(null).build();

        when(roleRepository.getById(1L)).thenReturn(fetched);

        userCommandHandler.validate(user);

        verify(roleRepository).getById(1L);
        assertThat(user.getRoles()).containsExactly(fetched);
    }

    @Test
    void validate_withGroups_fetchesAndSetsGroups() {
        Group inputGroup = Group.builder().id(1L).build();
        Group fetched = adminGroup();
        User user = User.builder().roles(null).groups(new ArrayList<>(List.of(inputGroup))).build();

        when(groupRepository.getById(1L)).thenReturn(fetched);

        userCommandHandler.validate(user);

        verify(groupRepository).getById(1L);
        assertThat(user.getGroups()).containsExactly(fetched);
    }

    @Test
    void validate_withNullCollections_skipsAllValidation() {
        User user = User.builder().roles(null).groups(null).build();

        userCommandHandler.validate(user);

        verifyNoInteractions(roleRepository);
        verifyNoInteractions(groupRepository);
    }

    @Test
    void validate_withEmptyCollections_skipsAllValidation() {
        User user = User.builder().roles(Collections.emptyList()).groups(Collections.emptyList()).build();

        userCommandHandler.validate(user);

        verifyNoInteractions(roleRepository);
        verifyNoInteractions(groupRepository);
    }
}
