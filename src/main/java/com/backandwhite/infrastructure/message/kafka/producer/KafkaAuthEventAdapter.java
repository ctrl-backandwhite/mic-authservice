package com.backandwhite.infrastructure.message.kafka.producer;

import com.backandwhite.application.port.out.AuthEventPort;
import com.backandwhite.application.port.out.CustomerRegisteredRequest;
import com.backandwhite.common.constants.AppConstants;
import com.backandwhite.core.kafka.avro.CustomerRegisteredEvent;
import com.backandwhite.infrastructure.message.kafka.mapper.AuthEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Publishes customer lifecycle events to Kafka.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
public class KafkaAuthEventAdapter implements AuthEventPort {

    private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;
    private final AuthEventMapper authEventMapper;

    @Override
    public void publishCustomerRegistered(CustomerRegisteredRequest request) {
        CustomerRegisteredEvent event = authEventMapper.toCustomerRegisteredEvent(request);

        kafkaTemplate.send(AppConstants.KAFKA_TOPIC_CUSTOMER_REGISTERED, request.userId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish customer.registered for userId={}: {}", request.userId(),
                                ex.getMessage());
                    } else {
                        log.info("Published customer.registered for userId={}", request.userId());
                    }
                });
    }
}
