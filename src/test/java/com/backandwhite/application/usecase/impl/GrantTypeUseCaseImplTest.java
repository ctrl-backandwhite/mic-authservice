package com.backandwhite.application.usecase.impl;

import static com.backandwhite.provider.GrantTypeProvider.grantType;
import static com.backandwhite.provider.GrantTypeProvider.otherGrantType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backandwhite.application.handler.GrantTypeCommandHandler;
import com.backandwhite.application.mapper.GrantTypeUpdateMapper;
import com.backandwhite.common.exception.EntityNotFoundException;
import com.backandwhite.domain.model.GrantType;
import com.backandwhite.domain.repository.GrantTypeRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

@ExtendWith(MockitoExtension.class)
class GrantTypeUseCaseImplTest {

    @Mock
    private GrantTypeRepository grantTypeRepository;

    @Mock
    private GrantTypeCommandHandler grantTypeCommandHandler;

    @Mock
    private GrantTypeUpdateMapper grantTypeUpdateMapper;

    @InjectMocks
    private GrantTypeUseCaseImpl grantTypeUseCase;

    @Test
    void save_validGrantType_delegatesToHandlerAndRepository() {
        GrantType input = grantType().withId(null);
        GrantType saved = grantType();

        when(grantTypeRepository.save(input)).thenReturn(saved);

        GrantType result = grantTypeUseCase.save(input);

        assertSame(saved, result);
        verify(grantTypeCommandHandler).validate(input);
        verify(grantTypeRepository).save(input);
    }

    @Test
    void findAll_returnsRepositoryList() {
        List<GrantType> grantTypes = List.of(grantType(), otherGrantType());

        when(grantTypeRepository.findAll()).thenReturn(grantTypes);

        List<GrantType> result = grantTypeUseCase.findAll();

        assertSame(grantTypes, result);
        verify(grantTypeRepository).findAll();
    }

    @Test
    void getById_existingGrantType_returnsGrantType() {
        GrantType model = grantType().withId(5L);

        when(grantTypeRepository.getById(5L)).thenReturn(model);

        GrantType result = grantTypeUseCase.getById(5L);

        assertSame(model, result);
        verify(grantTypeRepository).getById(5L);
    }

    @Test
    void getById_missingGrantType_throwsEntityNotFound() {
        when(grantTypeRepository.getById(10L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> grantTypeUseCase.getById(10L));
        verify(grantTypeRepository).getById(10L);
    }

    @Test
    void update_existingGrantType_copiesFieldsAndPersists() {
        GrantType existing = grantType().withId(10L);
        GrantType update = otherGrantType().withId(99L);

        when(grantTypeRepository.getById(10L)).thenReturn(existing);
        doAnswer(inv -> {
            BeanUtils.copyProperties(inv.getArgument(0), inv.getArgument(1), "id");
            return null;
        }).when(grantTypeUpdateMapper).updateFromModel(any(GrantType.class), any(GrantType.class));
        when(grantTypeRepository.update(any(GrantType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GrantType result = grantTypeUseCase.update(update, 10L);

        verify(grantTypeRepository).update(any(GrantType.class));
        assertThat(result).usingRecursiveComparison().isEqualTo(otherGrantType().withId(10L));
    }

    @Test
    void delete_existingGrantType_delegatesToRepository() {
        GrantType existing = otherGrantType().withId(7L);

        when(grantTypeRepository.getById(7L)).thenReturn(existing);

        grantTypeUseCase.delete(7L);

        verify(grantTypeRepository).delete(7L);
    }
}
