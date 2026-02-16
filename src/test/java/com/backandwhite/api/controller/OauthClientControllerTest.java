package com.backandwhite.api.controller;

import com.backandwhite.api.dto.in.OauthClientDtoIn;
import com.backandwhite.api.dto.out.OauthClientDtoOut;
import com.backandwhite.api.mapper.OauthClientDtoMapper;
import com.backandwhite.application.usecase.OauthClientUseCase;
import com.backandwhite.domain.model.OauthClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.backandwhite.provider.OauthClientProvider.CLIENT_ID;
import static com.backandwhite.provider.OauthClientProvider.oauthClient;
import static com.backandwhite.provider.OauthClientProvider.oauthClientDtoIn;
import static com.backandwhite.provider.OauthClientProvider.oauthClientDtoOut;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OauthClientControllerTest {

    @Mock
    private OauthClientDtoMapper mapper;

    @Mock
    private OauthClientUseCase useCase;

    @InjectMocks
    private OauthClientController controller;

    @Test
    void create_returnsCreatedDto() {
        OauthClientDtoIn dtoIn = oauthClientDtoIn();
        OauthClient model = oauthClient();
        OauthClientDtoOut dtoOut = oauthClientDtoOut(CLIENT_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.save(model)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<OauthClientDtoOut> response = controller.create(dtoIn);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).save(model);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void update_returnsUpdatedDto() {
        OauthClientDtoIn dtoIn = oauthClientDtoIn();
        OauthClient model = oauthClient();
        OauthClientDtoOut dtoOut = oauthClientDtoOut(CLIENT_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.update(model, CLIENT_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<OauthClientDtoOut> response = controller.update(dtoIn, CLIENT_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).update(model, CLIENT_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete(CLIENT_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(useCase).delete(CLIENT_ID);
    }

    @Test
    void getById_returnsDto() {
        OauthClient model = oauthClient();
        OauthClientDtoOut dtoOut = oauthClientDtoOut(CLIENT_ID);

        when(useCase.getById(CLIENT_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<OauthClientDtoOut> response = controller.getById(CLIENT_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(useCase).getById(CLIENT_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void findAll_returnsDtoList() {
        List<OauthClient> models = List.of(oauthClient());
        List<OauthClientDtoOut> dtoOuts = List.of(oauthClientDtoOut(CLIENT_ID));

        when(useCase.findAll()).thenReturn(models);
        when(mapper.toDtoOutList(models)).thenReturn(dtoOuts);

        ResponseEntity<List<OauthClientDtoOut>> response = controller.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOuts);
        verify(useCase).findAll();
        verify(mapper).toDtoOutList(models);
    }
}
