package com.backandwhite.application.usecase.impl;

import com.backandwhite.application.handler.OauthClientCommandHandler;
import com.backandwhite.common.exception.EntityNotFoundException;
import com.backandwhite.domain.model.OauthClient;
import com.backandwhite.application.mapper.OauthClientUpdateMapper;
import com.backandwhite.domain.repository.OauthClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.backandwhite.provider.OauthClientProvider.oauthClient;
import static com.backandwhite.provider.OauthClientProvider.otherOauthClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.BeanUtils;

@ExtendWith(MockitoExtension.class)
class OauthClientUseCaseImplTest {

    @Mock
    private OauthClientRepository oauthClientRepository;

    @Mock
    private OauthClientCommandHandler oauthClientCommandHandler;

    @Mock
    private OauthClientUpdateMapper oauthClientUpdateMapper;

    @InjectMocks
    private OauthClientUseCaseImpl oauthClientUseCase;

    @Test
    void save_validOauthClient_delegatesToHandlerAndRepository() {
        OauthClient input = oauthClient().withId(null);
        OauthClient saved = oauthClient();

        when(oauthClientRepository.save(input)).thenReturn(saved);

        OauthClient result = oauthClientUseCase.save(input);

        assertSame(saved, result);
        verify(oauthClientCommandHandler).validate(input);
        verify(oauthClientRepository).save(input);
    }

    @Test
    void findAll_returnsRepositoryList() {
        List<OauthClient> clients = List.of(
                oauthClient(),
                otherOauthClient());

        when(oauthClientRepository.findAll()).thenReturn(clients);

        List<OauthClient> result = oauthClientUseCase.findAll();

        assertSame(clients, result);
        verify(oauthClientRepository).findAll();
    }

    @Test
    void getById_existingOauthClient_returnsOauthClient() {
        OauthClient model = oauthClient().withId(5L);

        when(oauthClientRepository.getById(5L)).thenReturn(model);

        OauthClient result = oauthClientUseCase.getById(5L);

        assertSame(model, result);
        verify(oauthClientRepository).getById(5L);
    }

    @Test
    void getById_missingOauthClient_throwsEntityNotFound() {
        when(oauthClientRepository.getById(10L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> oauthClientUseCase.getById(10L));
        verify(oauthClientRepository).getById(10L);
    }

    @Test
    void update_existingOauthClient_copiesFieldsAndPersists() {
        OauthClient existing = oauthClient().withId(10L);
        OauthClient update = otherOauthClient().withId(99L);

        when(oauthClientRepository.getById(10L)).thenReturn(existing);
        doAnswer(inv -> { BeanUtils.copyProperties(inv.getArgument(0), inv.getArgument(1), "id", "createdAt", "updatedAt", "createdBy", "updatedBy"); return null; })
                .when(oauthClientUpdateMapper).updateFromModel(any(OauthClient.class), any(OauthClient.class));
        when(oauthClientRepository.update(any(OauthClient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OauthClient result = oauthClientUseCase.update(update, 10L);

        verify(oauthClientRepository).update(any(OauthClient.class));
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(otherOauthClient().withId(10L));
    }

    @Test
    void delete_existingOauthClient_delegatesToRepository() {
        OauthClient existing = otherOauthClient().withId(7L);

        when(oauthClientRepository.getById(7L)).thenReturn(existing);

        oauthClientUseCase.delete(7L);

        verify(oauthClientRepository).delete(7L);
    }
}
