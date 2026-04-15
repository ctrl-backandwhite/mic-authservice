package com.backandwhite.infrastructure.message.kafka.producer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.backandwhite.application.port.out.EmailNotificationRequest;
import java.util.Map;
import org.junit.jupiter.api.Test;

class NoOpNotificationEventAdapterTest {

    private final NoOpNotificationEventAdapter adapter = new NoOpNotificationEventAdapter();

    @Test
    void sendNotificationEvent_doesNothing() {
        EmailNotificationRequest request = new EmailNotificationRequest("user@test.com", "Subject", "template",
                Map.of());

        assertDoesNotThrow(() -> adapter.sendNotificationEvent(request));
    }
}
