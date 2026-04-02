package com.backandwhite.api.controller;

import com.backandwhite.api.dto.in.GrantTypeDtoIn;
import com.backandwhite.api.dto.out.GrantTypeDtoOut;
import com.backandwhite.api.mapper.GrantTypeDtoMapper;
import com.backandwhite.application.usecase.GrantTypeUseCase;
import com.backandwhite.domain.model.GrantType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.backandwhite.provider.GrantTypeProvider.GRANT_TYPE_ID;
import static com.backandwhite.provider.GrantTypeProvider.grantType;
import static com.backandwhite.provider.GrantTypeProvider.grantTypeDtoIn;
import static com.backandwhite.provider.GrantTypeProvider.grantTypeDtoOut;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantTypeControllerTest {

    @Mock
    private GrantTypeDtoMapper mapper;

    @Mock
    private GrantTypeUseCase useCase;

    @InjectMocks
    private GrantTypeController controller;

    @Test
    void create_returnsCreatedDto() {
        GrantTypeDtoIn dtoIn = grantTypeDtoIn();
        GrantType model = grantType();
        GrantTypeDtoOut dtoOut = grantTypeDtoOut(GRANT_TYPE_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.save(model)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<GrantTypeDtoOut> response = controller.create("test-nx-token", dtoIn);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).save(model);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void update_returnsUpdatedDto() {
        GrantTypeDtoIn dtoIn = grantTypeDtoIn();
        GrantType model = grantType();
        GrantTypeDtoOut dtoOut = grantTypeDtoOut(GRANT_TYPE_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.update(model, GRANT_TYPE_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<GrantTypeDtoOut> response = controller.update("test-nx-token", dtoIn, GRANT_TYPE_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).update(model, GRANT_TYPE_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete("test-nx-token", GRANT_TYPE_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(useCase).delete(GRANT_TYPE_ID);
    }

    @Test
    void getById_returnsDto() {
        GrantType model = grantType();
        GrantTypeDtoOut dtoOut = grantTypeDtoOut(GRANT_TYPE_ID);

        when(useCase.getById(GRANT_TYPE_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<GrantTypeDtoOut> response = controller.getById("test-nx-token", GRANT_TYPE_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(useCase).getById(GRANT_TYPE_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void findAll_returnsDtoList() {
        List<GrantType> models = List.of(grantType());
        List<GrantTypeDtoOut> dtoOuts = List.of(grantTypeDtoOut(GRANT_TYPE_ID));

        when(useCase.findAll()).thenReturn(models);
        when(mapper.toDtoOutList(models)).thenReturn(dtoOuts);

        ResponseEntity<List<GrantTypeDtoOut>> response = controller.findAll("test-nx-token", null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOuts);
        verify(useCase).findAll();
        verify(mapper).toDtoOutList(models);
    }

    @Test
    void findAll_withEnabledFilter_returnsOnlyEnabled() {
        GrantType enabledGt = GrantType.builder().enabled(Boolean.TRUE).build();
        GrantType disabledGt = GrantType.builder().enabled(Boolean.FALSE).build();
        List<GrantType> onlyEnabled = List.of(enabledGt);
        List<GrantTypeDtoOut> dtoOuts = List.of(grantTypeDtoOut(GRANT_TYPE_ID));

        when(useCase.findAll()).thenReturn(List.of(enabledGt, disabledGt));
        when(mapper.toDtoOutList(onlyEnabled)).thenReturn(dtoOuts);

        ResponseEntity<List<GrantTypeDtoOut>> response = controller.findAll("test-nx-token", Boolean.TRUE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOuts);
        verify(useCase).findAll();
        verify(mapper).toDtoOutList(onlyEnabled);
    }
}
