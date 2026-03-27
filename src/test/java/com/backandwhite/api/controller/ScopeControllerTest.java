package com.backandwhite.api.controller;

import com.backandwhite.api.dto.in.ScopeDtoIn;
import com.backandwhite.api.dto.out.ScopeDtoOut;
import com.backandwhite.api.mapper.ScopeDtoMapper;
import com.backandwhite.application.usecase.ScopeUseCase;
import com.backandwhite.domain.model.Scope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.backandwhite.provider.ScopeProvider.READ_ID;
import static com.backandwhite.provider.ScopeProvider.readScope;
import static com.backandwhite.provider.ScopeProvider.readScopeDtoIn;
import static com.backandwhite.provider.ScopeProvider.readScopeDtoOut;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScopeControllerTest {

    @Mock
    private ScopeDtoMapper mapper;

    @Mock
    private ScopeUseCase useCase;

    @InjectMocks
    private ScopeController controller;

    @Test
    void create_returnsCreatedDto() {
        ScopeDtoIn dtoIn = readScopeDtoIn();
        Scope model = readScope();
        ScopeDtoOut dtoOut = readScopeDtoOut(READ_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.save(model)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<ScopeDtoOut> response = controller.create(dtoIn);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).save(model);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void update_returnsUpdatedDto() {
        ScopeDtoIn dtoIn = readScopeDtoIn();
        Scope model = readScope();
        ScopeDtoOut dtoOut = readScopeDtoOut(READ_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.update(model, READ_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<ScopeDtoOut> response = controller.update(dtoIn, READ_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).update(model, READ_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete(READ_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(useCase).delete(READ_ID);
    }

    @Test
    void getById_returnsDto() {
        Scope model = readScope();
        ScopeDtoOut dtoOut = readScopeDtoOut(READ_ID);

        when(useCase.getById(READ_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<ScopeDtoOut> response = controller.getById(READ_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(useCase).getById(READ_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void findAll_returnsDtoList() {
        List<Scope> models = List.of(readScope());
        List<ScopeDtoOut> dtoOuts = List.of(readScopeDtoOut(READ_ID));

        when(useCase.findAll()).thenReturn(models);
        when(mapper.toDtoOutList(models)).thenReturn(dtoOuts);

        ResponseEntity<List<ScopeDtoOut>> response = controller.findAll(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOuts);
        verify(useCase).findAll();
        verify(mapper).toDtoOutList(models);
    }

    @Test
    void findAll_withEnabledFilter_returnsOnlyEnabled() {
        Scope enabledScope = Scope.builder().enabled(Boolean.TRUE).build();
        Scope disabledScope = Scope.builder().enabled(Boolean.FALSE).build();
        List<Scope> onlyEnabled = List.of(enabledScope);
        List<ScopeDtoOut> dtoOuts = List.of(readScopeDtoOut(READ_ID));

        when(useCase.findAll()).thenReturn(List.of(enabledScope, disabledScope));
        when(mapper.toDtoOutList(onlyEnabled)).thenReturn(dtoOuts);

        ResponseEntity<List<ScopeDtoOut>> response = controller.findAll(Boolean.TRUE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOuts);
        verify(useCase).findAll();
        verify(mapper).toDtoOutList(onlyEnabled);
    }
}
