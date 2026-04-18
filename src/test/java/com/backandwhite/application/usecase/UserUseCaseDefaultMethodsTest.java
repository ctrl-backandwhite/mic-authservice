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
}
