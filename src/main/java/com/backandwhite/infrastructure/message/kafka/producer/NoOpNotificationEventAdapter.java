package com.backandwhite.infrastructure.message.kafka.producer;

import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.core.kafka.avro.EmailNotificationEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpNotificationEventAdapter implements NotificationEventPort {

    @Override
    public void sendNotificationEvent(EmailNotificationEvent event) {
        // Intentionally empty — NoOp implementation when Kafka is disabled
    }
}
