package com.backandwhite.api.controller;

import com.backandwhite.api.dto.in.GroupDtoIn;
import com.backandwhite.api.dto.out.GroupDtoOut;
import com.backandwhite.api.mapper.GroupDtoMapper;
import com.backandwhite.application.usecase.GroupUseCase;
import com.backandwhite.domain.model.Group;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.backandwhite.provider.GroupProvider.ADMIN_ID;
import static com.backandwhite.provider.GroupProvider.adminGroup;
import static com.backandwhite.provider.GroupProvider.adminGroupDtoIn;
import static com.backandwhite.provider.GroupProvider.adminGroupDtoOut;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    @Mock
    private GroupDtoMapper mapper;

    @Mock
    private GroupUseCase useCase;

    @InjectMocks
    private GroupController controller;

    @Test
    void create_returnsCreatedDto() {
        GroupDtoIn dtoIn = adminGroupDtoIn();
        Group model = adminGroup();
        GroupDtoOut dtoOut = adminGroupDtoOut(ADMIN_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.save(model)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<GroupDtoOut> response = controller.create(dtoIn);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).save(model);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void update_returnsUpdatedDto() {
        GroupDtoIn dtoIn = adminGroupDtoIn();
        Group model = adminGroup();
        GroupDtoOut dtoOut = adminGroupDtoOut(ADMIN_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.update(model, ADMIN_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<GroupDtoOut> response = controller.update(dtoIn, ADMIN_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).update(model, ADMIN_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete(ADMIN_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(useCase).delete(ADMIN_ID);
    }

    @Test
    void getById_returnsDto() {
        Group model = adminGroup();
        GroupDtoOut dtoOut = adminGroupDtoOut(ADMIN_ID);

        when(useCase.getById(ADMIN_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<GroupDtoOut> response = controller.getById(ADMIN_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(useCase).getById(ADMIN_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void findAll_returnsDtoList() {
        List<Group> models = List.of(adminGroup());
        List<GroupDtoOut> dtoOuts = List.of(adminGroupDtoOut(ADMIN_ID));

        when(useCase.findAll()).thenReturn(models);
        when(mapper.toDtoOutList(models)).thenReturn(dtoOuts);

        ResponseEntity<List<GroupDtoOut>> response = controller.findAll(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOuts);
        verify(useCase).findAll();
        verify(mapper).toDtoOutList(models);
    }

    @Test
    void findAll_withEnabledFilter_returnsOnlyEnabled() {
        Group enabledGroup = Group.builder().enabled(Boolean.TRUE).build();
        Group disabledGroup = Group.builder().enabled(Boolean.FALSE).build();
        List<Group> onlyEnabled = List.of(enabledGroup);
        List<GroupDtoOut> dtoOuts = List.of(adminGroupDtoOut(ADMIN_ID));

        when(useCase.findAll()).thenReturn(List.of(enabledGroup, disabledGroup));
        when(mapper.toDtoOutList(onlyEnabled)).thenReturn(dtoOuts);

        ResponseEntity<List<GroupDtoOut>> response = controller.findAll(Boolean.TRUE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOuts);
        verify(useCase).findAll();
        verify(mapper).toDtoOutList(onlyEnabled);
    }
}
