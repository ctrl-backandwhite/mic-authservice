package com.backandwhite.infrastructure.message.kafka.producer;

import com.backandwhite.application.port.out.AuthEventPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpAuthEventAdapter implements AuthEventPort {

    @Override
    public void publishCustomerRegistered(String userId, String email, String firstName, String lastName) {
        // Intentionally empty — NoOp implementation when Kafka is disabled
    }
}
