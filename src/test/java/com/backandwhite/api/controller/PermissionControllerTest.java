package com.backandwhite.api.controller;

import static com.backandwhite.provider.PermissionProvider.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.backandwhite.api.dto.in.PermissionDtoIn;
import com.backandwhite.api.dto.out.PermissionDtoOut;
import com.backandwhite.api.mapper.PermissionDtoMapper;
import com.backandwhite.application.usecase.PermissionUseCase;
import com.backandwhite.domain.model.Permission;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {
    @Mock
    private PermissionDtoMapper mapper;
    @Mock
    private PermissionUseCase useCase;
    @InjectMocks
    private PermissionController controller;

    @Test
    void create_returnsCreatedDto() {
        PermissionDtoIn dtoIn = permissionDtoIn();
        Permission model = permission();
        PermissionDtoOut dtoOut = permissionDtoOut(PERMISSION_ID);
        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.save(model)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);
        ResponseEntity<PermissionDtoOut> response = controller.create(dtoIn);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).save(model);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void update_returnsUpdatedDto() {
        PermissionDtoIn dtoIn = permissionDtoIn();
        Permission model = permission();
        PermissionDtoOut dtoOut = permissionDtoOut(PERMISSION_ID);
        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.update(model, PERMISSION_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);
        ResponseEntity<PermissionDtoOut> response = controller.update(dtoIn, PERMISSION_ID);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete(PERMISSION_ID);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(useCase).delete(PERMISSION_ID);
    }

    @Test
    void getById_returnsDto() {
        Permission model = permission();
        PermissionDtoOut dtoOut = permissionDtoOut(PERMISSION_ID);
        when(useCase.getById(PERMISSION_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);
        ResponseEntity<PermissionDtoOut> response = controller.getById(PERMISSION_ID);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
    }

    @Test
    void findAll_returnsDtoList() {
        List<Permission> models = List.of(permission());
        List<PermissionDtoOut> dtoOuts = List.of(permissionDtoOut(PERMISSION_ID));
        when(useCase.findAll()).thenReturn(models);
        when(mapper.toDtoOutList(models)).thenReturn(dtoOuts);
        ResponseEntity<List<PermissionDtoOut>> response = controller.findAll(null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOuts);
    }

    @Test
    void findAll_withEnabledFilter_returnsOnlyEnabled() {
        Permission enabled = Permission.builder().enabled(Boolean.TRUE).build();
        Permission disabled = Permission.builder().enabled(Boolean.FALSE).build();
        List<Permission> onlyEnabled = List.of(enabled);
        List<PermissionDtoOut> dtoOuts = List.of(permissionDtoOut(PERMISSION_ID));
        when(useCase.findAll()).thenReturn(List.of(enabled, disabled));
        when(mapper.toDtoOutList(onlyEnabled)).thenReturn(dtoOuts);
        ResponseEntity<List<PermissionDtoOut>> response = controller.findAll(Boolean.TRUE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOuts);
    }
}
