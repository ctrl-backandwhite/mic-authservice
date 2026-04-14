package com.backandwhite.infrastructure.message.kafka.producer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backandwhite.common.constants.AppConstants;
import com.backandwhite.core.kafka.avro.EmailNotificationEvent;
import java.util.concurrent.CompletableFuture;
import org.apache.avro.specific.SpecificRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
class KafkaNotificationEventAdapterTest {

    @Mock
    private KafkaTemplate<String, SpecificRecord> kafkaTemplate;

    @InjectMocks
    private KafkaNotificationEventAdapter adapter;

    @SuppressWarnings("unchecked")
    @Test
    void sendNotificationEvent_sendsToKafka() {
        EmailNotificationEvent event = mock(EmailNotificationEvent.class);
        when(event.getRecipient()).thenReturn("user@test.com");

        CompletableFuture<SendResult<String, SpecificRecord>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(eq(AppConstants.KAFKA_TOPIC_NOTIFICATION_EMAIL), eq("user@test.com"), any()))
                .thenReturn(future);

        adapter.sendNotificationEvent(event);

        verify(kafkaTemplate).send(eq(AppConstants.KAFKA_TOPIC_NOTIFICATION_EMAIL), eq("user@test.com"), eq(event));
    }
}
