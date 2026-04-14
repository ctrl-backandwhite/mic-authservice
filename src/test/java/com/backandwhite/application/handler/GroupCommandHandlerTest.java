package com.backandwhite.application.handler;

import static com.backandwhite.provider.RoleProvider.adminRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.backandwhite.domain.model.Group;
import com.backandwhite.domain.model.Role;
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
class GroupCommandHandlerTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private GroupCommandHandler groupCommandHandler;

    @Test
    void validate_withRoles_fetchesAndSetsRoles() {
        Role inputRole = Role.builder().id(1L).build();
        Role fetchedRole = adminRole();
        Group group = Group.builder().roles(new ArrayList<>(List.of(inputRole))).build();

        when(roleRepository.getById(1L)).thenReturn(fetchedRole);

        groupCommandHandler.validate(group);

        verify(roleRepository).getById(1L);
        assertThat(group.getRoles()).containsExactly(fetchedRole);
    }

    @Test
    void validate_withNullRoles_skipsValidation() {
        Group group = Group.builder().roles(null).build();

        groupCommandHandler.validate(group);

        verifyNoInteractions(roleRepository);
    }

    @Test
    void validate_withEmptyRoles_skipsValidation() {
        Group group = Group.builder().roles(Collections.emptyList()).build();

        groupCommandHandler.validate(group);

        verifyNoInteractions(roleRepository);
    }
}
