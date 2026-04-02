package com.backandwhite.api.controller;

import com.backandwhite.api.dto.in.RedirectUriDtoIn;
import com.backandwhite.api.dto.out.RedirectUriDtoOut;
import com.backandwhite.api.mapper.RedirectUriDtoMapper;
import com.backandwhite.application.usecase.RedirectUriUseCase;
import com.backandwhite.domain.model.RedirectUri;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.backandwhite.provider.RedirectUriProvider.REDIRECT_URI_ID;
import static com.backandwhite.provider.RedirectUriProvider.redirectUri;
import static com.backandwhite.provider.RedirectUriProvider.redirectUriDtoIn;
import static com.backandwhite.provider.RedirectUriProvider.redirectUriDtoOut;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedirectUriControllerTest {

    @Mock
    private RedirectUriDtoMapper mapper;

    @Mock
    private RedirectUriUseCase useCase;

    @InjectMocks
    private RedirectUriController controller;

    @Test
    void create_returnsCreatedDto() {
        RedirectUriDtoIn dtoIn = redirectUriDtoIn();
        RedirectUri model = redirectUri();
        RedirectUriDtoOut dtoOut = redirectUriDtoOut(REDIRECT_URI_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.save(model)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<RedirectUriDtoOut> response = controller.create("test-nx-token", dtoIn);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).save(model);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void update_returnsUpdatedDto() {
        RedirectUriDtoIn dtoIn = redirectUriDtoIn();
        RedirectUri model = redirectUri();
        RedirectUriDtoOut dtoOut = redirectUriDtoOut(REDIRECT_URI_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.update(model, REDIRECT_URI_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<RedirectUriDtoOut> response = controller.update("test-nx-token", dtoIn, REDIRECT_URI_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).update(model, REDIRECT_URI_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete("test-nx-token", REDIRECT_URI_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(useCase).delete(REDIRECT_URI_ID);
    }

    @Test
    void getById_returnsDto() {
        RedirectUri model = redirectUri();
        RedirectUriDtoOut dtoOut = redirectUriDtoOut(REDIRECT_URI_ID);

        when(useCase.getById(REDIRECT_URI_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<RedirectUriDtoOut> response = controller.getById("test-nx-token", REDIRECT_URI_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(useCase).getById(REDIRECT_URI_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void findAll_returnsDtoList() {
        List<RedirectUri> models = List.of(redirectUri());
        List<RedirectUriDtoOut> dtoOuts = List.of(redirectUriDtoOut(REDIRECT_URI_ID));

        when(useCase.findAll()).thenReturn(models);
        when(mapper.toDtoOutList(models)).thenReturn(dtoOuts);

        ResponseEntity<List<RedirectUriDtoOut>> response = controller.findAll("test-nx-token", null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOuts);
        verify(useCase).findAll();
        verify(mapper).toDtoOutList(models);
    }

    @Test
    void findAll_withEnabledFilter_returnsOnlyEnabled() {
        RedirectUri enabledUri = RedirectUri.builder().enabled(Boolean.TRUE).build();
        RedirectUri disabledUri = RedirectUri.builder().enabled(Boolean.FALSE).build();
        List<RedirectUri> onlyEnabled = List.of(enabledUri);
        List<RedirectUriDtoOut> dtoOuts = List.of(redirectUriDtoOut(REDIRECT_URI_ID));

        when(useCase.findAll()).thenReturn(List.of(enabledUri, disabledUri));
        when(mapper.toDtoOutList(onlyEnabled)).thenReturn(dtoOuts);

        ResponseEntity<List<RedirectUriDtoOut>> response = controller.findAll("test-nx-token", Boolean.TRUE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOuts);
        verify(useCase).findAll();
        verify(mapper).toDtoOutList(onlyEnabled);
    }
}
