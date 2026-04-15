package com.backandwhite.application.port.out;

/**
 * Port interface for publishing notification events.
 */
public interface NotificationEventPort {

    /**
     * Sends a notification event (typically email) via the messaging
     * infrastructure.
     */
    void sendNotificationEvent(EmailNotificationRequest request);
}
