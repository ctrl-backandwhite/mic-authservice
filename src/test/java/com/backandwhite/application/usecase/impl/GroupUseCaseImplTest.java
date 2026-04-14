package com.backandwhite.application.usecase.impl;

import com.backandwhite.application.handler.GroupCommandHandler;
import com.backandwhite.common.exception.EntityNotFoundException;
import com.backandwhite.domain.model.Group;
import com.backandwhite.application.mapper.GroupUpdateMapper;
import com.backandwhite.domain.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.backandwhite.provider.GroupProvider.adminGroup;
import static com.backandwhite.provider.GroupProvider.userGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.BeanUtils;

@ExtendWith(MockitoExtension.class)
class GroupUseCaseImplTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupCommandHandler groupCommandHandler;

    @Mock
    private GroupUpdateMapper groupUpdateMapper;

    @InjectMocks
    private GroupUseCaseImpl groupUseCase;

    @Test
    void save_validGroup_delegatesToHandlerAndRepository() {
        Group input = adminGroup().withId(null);
        Group saved = adminGroup();

        when(groupRepository.save(input)).thenReturn(saved);

        Group result = groupUseCase.save(input);

        assertSame(saved, result);
        verify(groupCommandHandler).validate(input);
        verify(groupRepository).save(input);
    }

    @Test
    void findAll_returnsRepositoryList() {
        List<Group> groups = List.of(
                adminGroup(),
                userGroup());

        when(groupRepository.findAll()).thenReturn(groups);

        List<Group> result = groupUseCase.findAll();

        assertSame(groups, result);
        verify(groupRepository).findAll();
    }

    @Test
    void getById_existingGroup_returnsGroup() {
        Group group = adminGroup().withId(5L);

        when(groupRepository.getById(5L)).thenReturn(group);

        Group result = groupUseCase.getById(5L);

        assertSame(group, result);
        verify(groupRepository).getById(5L);
    }

    @Test
    void getById_missingGroup_throwsEntityNotFound() {
        when(groupRepository.getById(10L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> groupUseCase.getById(10L));
        verify(groupRepository).getById(10L);
    }

    @Test
    void update_existingGroup_copiesFieldsAndPersists() {
        Group existing = adminGroup().withId(10L);
        Group update = userGroup().withId(99L);

        when(groupRepository.getById(10L)).thenReturn(existing);
        doAnswer(inv -> { BeanUtils.copyProperties(inv.getArgument(0), inv.getArgument(1), "id", "createdAt", "updatedAt", "createdBy", "updatedBy"); return null; })
                .when(groupUpdateMapper).updateFromModel(any(Group.class), any(Group.class));
        when(groupRepository.update(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Group result = groupUseCase.update(update, 10L);

        verify(groupRepository).update(any(Group.class));
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(userGroup().withId(10L));
    }

    @Test
    void delete_existingGroup_delegatesToRepository() {
        Group existing = userGroup().withId(7L);

        when(groupRepository.getById(7L)).thenReturn(existing);

        groupUseCase.delete(7L);

        verify(groupRepository).delete(7L);
    }
}
