package com.backandwhite.application.port.out;

import com.backandwhite.core.kafka.avro.EmailNotificationEvent;

/**
 * Port interface for publishing notification events.
 */
public interface NotificationEventPort {

    /**
     * Sends a notification event (typically email) via the messaging
     * infrastructure.
     */
    void sendNotificationEvent(EmailNotificationEvent event);
}
