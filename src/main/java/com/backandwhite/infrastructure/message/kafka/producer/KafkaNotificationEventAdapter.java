package com.backandwhite.infrastructure.message.kafka.producer;

import com.backandwhite.application.port.out.EmailNotificationRequest;
import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.common.constants.AppConstants;
import com.backandwhite.core.kafka.avro.EmailNotificationEvent;
import com.backandwhite.infrastructure.message.kafka.mapper.NotificationEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
public class KafkaNotificationEventAdapter implements NotificationEventPort {

    private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;
    private final NotificationEventMapper notificationEventMapper;

    @Override
    public void sendNotificationEvent(EmailNotificationRequest request) {
        EmailNotificationEvent event = notificationEventMapper.toEmailNotificationEvent(request);
        log.debug("::> Publishing notification event to topic '{}' for recipient: {}",
                AppConstants.KAFKA_TOPIC_NOTIFICATION_EMAIL, request.recipient());
        kafkaTemplate.send(AppConstants.KAFKA_TOPIC_NOTIFICATION_EMAIL, request.recipient(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("::> Failed to publish notification event for {}: {}", request.recipient(),
                                ex.getMessage(), ex);
                    } else {
                        log.debug("::> Notification event published successfully for {}, offset: {}",
                                request.recipient(), result.getRecordMetadata().offset());
                    }
                });
    }
}
