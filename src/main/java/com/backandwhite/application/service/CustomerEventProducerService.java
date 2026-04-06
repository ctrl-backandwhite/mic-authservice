package com.backandwhite.application.service;

import com.backandwhite.common.constants.AppConstants;
import com.backandwhite.core.kafka.avro.CustomerRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Publishes customer lifecycle events to Kafka.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
public class CustomerEventProducerService {

    private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;

    /**
     * Publishes a customer.registered event after user registration (M-13).
     */
    public void publishCustomerRegistered(String userId, String email,
            String firstName, String lastName) {
        var event = CustomerRegisteredEvent.newBuilder()
                .setUserId(userId)
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setTimestamp(Instant.now().toString())
                .build();

        kafkaTemplate.send(
                AppConstants.KAFKA_TOPIC_CUSTOMER_REGISTERED,
                userId,
                event).whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish customer.registered for user={}: {}",
                                userId, ex.getMessage());
                    } else {
                        log.info("Published customer.registered for user={}, email={}",
                                userId, email);
                    }
                });
    }
}
