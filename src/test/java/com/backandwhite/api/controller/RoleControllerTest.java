package com.backandwhite.api.controller;

import com.backandwhite.api.dto.in.RoleDtoIn;
import com.backandwhite.api.dto.out.RoleDtoOut;
import com.backandwhite.api.mapper.RoleDtoMapper;
import com.backandwhite.application.usecase.RoleUseCase;
import com.backandwhite.domain.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.backandwhite.provider.RoleProvider.ROLE_ID;
import static com.backandwhite.provider.RoleProvider.role;
import static com.backandwhite.provider.RoleProvider.supportRoleDtoIn;
import static com.backandwhite.provider.RoleProvider.supportRoleDtoOut;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleDtoMapper mapper;

    @Mock
    private RoleUseCase useCase;

    @InjectMocks
    private RoleController controller;

    @Test
    void create_returnsCreatedDto() {
        RoleDtoIn dtoIn = supportRoleDtoIn();
        Role model = role();
        RoleDtoOut dtoOut = supportRoleDtoOut(ROLE_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.save(model)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<RoleDtoOut> response = controller.create(dtoIn);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).save(model);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void update_returnsUpdatedDto() {
        RoleDtoIn dtoIn = supportRoleDtoIn();
        Role model = role();
        RoleDtoOut dtoOut = supportRoleDtoOut(ROLE_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.update(model, ROLE_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<RoleDtoOut> response = controller.update(dtoIn, ROLE_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).update(model, ROLE_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete(ROLE_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(useCase).delete(ROLE_ID);
    }

    @Test
    void getById_returnsDto() {
        Role model = role();
        RoleDtoOut dtoOut = supportRoleDtoOut(ROLE_ID);

        when(useCase.getById(ROLE_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<RoleDtoOut> response = controller.getById(ROLE_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(useCase).getById(ROLE_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void findAll_returnsDtoList() {
        List<Role> models = List.of(role());
        List<RoleDtoOut> dtoOuts = List.of(supportRoleDtoOut(ROLE_ID));

        when(useCase.findAll()).thenReturn(models);
        when(mapper.toDtoOutList(models)).thenReturn(dtoOuts);

        ResponseEntity<List<RoleDtoOut>> response = controller.findAll(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOuts);
        verify(useCase).findAll();
        verify(mapper).toDtoOutList(models);
    }

    @Test
    void findAll_withEnabledFilter_returnsOnlyEnabled() {
        Role enabledRole = Role.builder().enabled(Boolean.TRUE).build();
        Role disabledRole = Role.builder().enabled(Boolean.FALSE).build();
        List<Role> onlyEnabled = List.of(enabledRole);
        List<RoleDtoOut> dtoOuts = List.of(supportRoleDtoOut(ROLE_ID));

        when(useCase.findAll()).thenReturn(List.of(enabledRole, disabledRole));
        when(mapper.toDtoOutList(onlyEnabled)).thenReturn(dtoOuts);

        ResponseEntity<List<RoleDtoOut>> response = controller.findAll(Boolean.TRUE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOuts);
        verify(useCase).findAll();
        verify(mapper).toDtoOutList(onlyEnabled);
    }
}
