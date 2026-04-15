package com.backandwhite.infrastructure.message.kafka.producer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.backandwhite.application.port.out.CustomerRegisteredRequest;
import org.junit.jupiter.api.Test;

class NoOpAuthEventAdapterTest {

    private final NoOpAuthEventAdapter adapter = new NoOpAuthEventAdapter();

    @Test
    void publishCustomerRegistered_doesNothing() {
        assertDoesNotThrow(
                () -> adapter.publishCustomerRegistered(new CustomerRegisteredRequest("1", "test@test.com", "John", "Doe")));
    }
}
