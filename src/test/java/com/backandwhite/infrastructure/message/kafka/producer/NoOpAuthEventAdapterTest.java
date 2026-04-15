package com.backandwhite.infrastructure.message.kafka.producer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class NoOpAuthEventAdapterTest {

    private final NoOpAuthEventAdapter adapter = new NoOpAuthEventAdapter();

    @Test
    void publishCustomerRegistered_doesNothing() {
        assertDoesNotThrow(
                () -> adapter.publishCustomerRegistered("1", "test@test.com", "John", "Doe"));
    }
}
