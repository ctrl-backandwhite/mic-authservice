package com.backandwhite.infrastructure.message.kafka.producer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import com.backandwhite.core.kafka.avro.EmailNotificationEvent;
import org.junit.jupiter.api.Test;

class NoOpNotificationEventAdapterTest {

    private final NoOpNotificationEventAdapter adapter = new NoOpNotificationEventAdapter();

    @Test
    void sendNotificationEvent_doesNothing() {
        EmailNotificationEvent event = mock(EmailNotificationEvent.class);

        assertDoesNotThrow(() -> adapter.sendNotificationEvent(event));
    }
}
