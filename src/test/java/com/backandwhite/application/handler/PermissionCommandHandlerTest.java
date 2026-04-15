package com.backandwhite.application.handler;

import static com.backandwhite.provider.PermissionProvider.permission;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PermissionCommandHandlerTest {

    @InjectMocks
    private PermissionCommandHandler permissionCommandHandler;

    @Test
    void validate_doesNotThrow() {
        assertDoesNotThrow(() -> permissionCommandHandler.validate(permission()));
    }
}
