package com.backandwhite.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.backandwhite.domain.model.User;
import org.junit.jupiter.api.Test;

class UserUseCaseDefaultMethodsTest {

    @Test
    void findUserByEmail_defaultReturnsNull() {
        UserUseCase useCase = spy(UserUseCase.class);

        User result = useCase.findUserByEmail("test@test.com");

        assertThat(result).isNull();
    }

    @Test
    void activateUser_withLang_delegatesToActivateUser() {
        UserUseCase useCase = spy(UserUseCase.class);
        doNothing().when(useCase).activateUser("token-123");

        useCase.activateUser("token-123", "es");

        verify(useCase).activateUser("token-123");
    }

    @Test
    void requestPasswordReset_withLang_delegatesToRequestPasswordReset() {
        UserUseCase useCase = spy(UserUseCase.class);
        doNothing().when(useCase).requestPasswordReset("test@test.com");

        useCase.requestPasswordReset("test@test.com", "en");

        verify(useCase).requestPasswordReset("test@test.com");
    }

    @Test
    void save_withLang_delegatesToSave() {
        UserUseCase useCase = spy(UserUseCase.class);
        User user = User.builder().name("Ana").build();
        User savedUser = User.builder().id(1L).name("Ana").build();
        when(useCase.save(user)).thenReturn(savedUser);

        User result = useCase.save(user, "es");

        assertThat(result).isEqualTo(savedUser);
        verify(useCase).save(user);
    }
}
