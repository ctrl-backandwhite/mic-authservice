package com.backandwhite.application.usecase.impl;

import com.backandwhite.application.handler.RedirectUriCommandHandler;
import com.backandwhite.common.exception.EntityNotFoundException;
import com.backandwhite.domain.model.RedirectUri;
import com.backandwhite.domain.repository.RedirectUriRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.backandwhite.provider.RedirectUriProvider.otherRedirectUri;
import static com.backandwhite.provider.RedirectUriProvider.redirectUri;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedirectUriUseCaseImplTest {

    @Mock
    private RedirectUriRepository redirectUriRepository;

    @Mock
    private RedirectUriCommandHandler redirectUriCommandHandler;

    @InjectMocks
    private RedirectUriUseCaseImpl redirectUriUseCase;

    @Test
    void save_validRedirectUri_delegatesToHandlerAndRepository() {
        RedirectUri input = redirectUri().withId(null);
        RedirectUri saved = redirectUri();

        when(redirectUriRepository.save(input)).thenReturn(saved);

        RedirectUri result = redirectUriUseCase.save(input);

        assertSame(saved, result);
        verify(redirectUriCommandHandler).validate(input);
        verify(redirectUriRepository).save(input);
    }

    @Test
    void findAll_returnsRepositoryList() {
        List<RedirectUri> redirectUris = List.of(
                redirectUri(),
                otherRedirectUri());

        when(redirectUriRepository.findAll()).thenReturn(redirectUris);

        List<RedirectUri> result = redirectUriUseCase.findAll();

        assertSame(redirectUris, result);
        verify(redirectUriRepository).findAll();
    }

    @Test
    void getById_existingRedirectUri_returnsRedirectUri() {
        RedirectUri model = redirectUri().withId(5L);

        when(redirectUriRepository.getById(5L)).thenReturn(model);

        RedirectUri result = redirectUriUseCase.getById(5L);

        assertSame(model, result);
        verify(redirectUriRepository).getById(5L);
    }

    @Test
    void getById_missingRedirectUri_throwsEntityNotFound() {
        when(redirectUriRepository.getById(10L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> redirectUriUseCase.getById(10L));
        verify(redirectUriRepository).getById(10L);
    }

    @Test
    void update_existingRedirectUri_copiesFieldsAndPersists() {
        RedirectUri existing = redirectUri().withId(10L);
        RedirectUri update = otherRedirectUri().withId(99L);

        when(redirectUriRepository.getById(10L)).thenReturn(existing);
        when(redirectUriRepository.update(any(RedirectUri.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RedirectUri result = redirectUriUseCase.update(update, 10L);

        verify(redirectUriRepository).update(any(RedirectUri.class));
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(otherRedirectUri().withId(10L));
    }

    @Test
    void delete_existingRedirectUri_delegatesToRepository() {
        RedirectUri existing = otherRedirectUri().withId(7L);

        when(redirectUriRepository.getById(7L)).thenReturn(existing);

        redirectUriUseCase.delete(7L);

        verify(redirectUriRepository).delete(7L);
    }
}
