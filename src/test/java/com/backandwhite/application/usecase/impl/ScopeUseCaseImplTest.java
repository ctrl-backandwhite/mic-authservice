package com.backandwhite.application.usecase.impl;

import static com.backandwhite.provider.ScopeProvider.readScope;
import static com.backandwhite.provider.ScopeProvider.writeScope;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backandwhite.application.handler.ScopeCommandHandler;
import com.backandwhite.application.mapper.ScopeUpdateMapper;
import com.backandwhite.common.exception.EntityNotFoundException;
import com.backandwhite.domain.model.Scope;
import com.backandwhite.domain.repository.ScopeRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

@ExtendWith(MockitoExtension.class)
class ScopeUseCaseImplTest {

    @Mock
    private ScopeRepository scopeRepository;

    @Mock
    private ScopeCommandHandler scopeCommandHandler;

    @Mock
    private ScopeUpdateMapper scopeUpdateMapper;

    @InjectMocks
    private ScopeUseCaseImpl scopeUseCase;

    @Test
    void save_validScope_delegatesToHandlerAndRepository() {
        Scope input = readScope().withId(null);
        Scope saved = readScope();

        when(scopeRepository.save(input)).thenReturn(saved);

        Scope result = scopeUseCase.save(input);

        assertSame(saved, result);
        verify(scopeCommandHandler).validate(input);
        verify(scopeRepository).save(input);
    }

    @Test
    void findAll_returnsRepositoryList() {
        List<Scope> scopes = List.of(readScope(), writeScope());

        when(scopeRepository.findAll()).thenReturn(scopes);

        List<Scope> result = scopeUseCase.findAll();

        assertSame(scopes, result);
        verify(scopeRepository).findAll();
    }

    @Test
    void getById_existingScope_returnsScope() {
        Scope scope = readScope().withId(5L);

        when(scopeRepository.getById(5L)).thenReturn(scope);

        Scope result = scopeUseCase.getById(5L);

        assertSame(scope, result);
        verify(scopeRepository).getById(5L);
    }

    @Test
    void getById_missingScope_throwsEntityNotFound() {
        when(scopeRepository.getById(10L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> scopeUseCase.getById(10L));
        verify(scopeRepository).getById(10L);
    }

    @Test
    void update_existingScope_copiesFieldsAndPersists() {
        Scope existing = readScope().withId(10L);
        Scope update = writeScope().withId(99L);

        when(scopeRepository.getById(10L)).thenReturn(existing);
        doAnswer(inv -> {
            BeanUtils.copyProperties(inv.getArgument(0), inv.getArgument(1), "id");
            return null;
        }).when(scopeUpdateMapper).updateFromModel(any(Scope.class), any(Scope.class));
        when(scopeRepository.update(any(Scope.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Scope result = scopeUseCase.update(update, 10L);

        verify(scopeRepository).update(any(Scope.class));
        assertThat(result).usingRecursiveComparison().isEqualTo(writeScope().withId(10L));
    }

    @Test
    void delete_existingScope_delegatesToRepository() {
        Scope existing = writeScope().withId(7L);

        when(scopeRepository.getById(7L)).thenReturn(existing);

        scopeUseCase.delete(7L);

        verify(scopeRepository).delete(7L);
    }
}
