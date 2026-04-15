package com.backandwhite.application.handler;

import static com.backandwhite.provider.GrantTypeProvider.grantType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GrantTypeCommandHandlerTest {

    @InjectMocks
    private GrantTypeCommandHandler grantTypeCommandHandler;

    @Test
    void validate_doesNotThrow() {
        assertDoesNotThrow(() -> grantTypeCommandHandler.validate(grantType()));
    }
}
