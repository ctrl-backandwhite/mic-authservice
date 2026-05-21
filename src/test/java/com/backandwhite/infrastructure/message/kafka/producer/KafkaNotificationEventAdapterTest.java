package com.backandwhite.infrastructure.message.kafka.producer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backandwhite.application.port.out.EmailNotificationRequest;
import com.backandwhite.common.constants.AppConstants;
import com.backandwhite.core.kafka.avro.EmailNotificationEvent;
import com.backandwhite.infrastructure.message.kafka.mapper.NotificationEventMapper;
import java.util.Map;
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

    @Mock
    private NotificationEventMapper notificationEventMapper;

    @InjectMocks
    private KafkaNotificationEventAdapter adapter;

    @SuppressWarnings("unchecked")
    @Test
    void sendNotificationEvent_sendsToKafka() {
        EmailNotificationRequest request = new EmailNotificationRequest("user@test.com", "Subject", "template",
                Map.of("name", "Ana"));

        EmailNotificationEvent event = EmailNotificationEvent.newBuilder().setRecipient("user@test.com")
                .setSubject("Subject").setTemplateName("template").setVariables(Map.of("name", "Ana")).build();
        when(notificationEventMapper.toEmailNotificationEvent(request)).thenReturn(event);

        CompletableFuture<SendResult<String, SpecificRecord>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(eq(AppConstants.KAFKA_TOPIC_NOTIFICATION_EMAIL), eq("user@test.com"), any()))
                .thenReturn(future);

        adapter.sendNotificationEvent(request);

        verify(notificationEventMapper).toEmailNotificationEvent(request);
        verify(kafkaTemplate).send(AppConstants.KAFKA_TOPIC_NOTIFICATION_EMAIL, "user@test.com", event);
    }

    @SuppressWarnings("unchecked")
    @Test
    void sendNotificationEvent_whenSuccess_logsDebug() {
        EmailNotificationRequest request = new EmailNotificationRequest("user@test.com", "Subject", "template",
                Map.of("name", "Ana"));

        EmailNotificationEvent event = EmailNotificationEvent.newBuilder().setRecipient("user@test.com")
                .setSubject("Subject").setTemplateName("template").setVariables(Map.of("name", "Ana")).build();
        when(notificationEventMapper.toEmailNotificationEvent(request)).thenReturn(event);

        CompletableFuture<SendResult<String, SpecificRecord>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(eq(AppConstants.KAFKA_TOPIC_NOTIFICATION_EMAIL), eq("user@test.com"), any()))
                .thenReturn(future);

        adapter.sendNotificationEvent(request);

        SendResult<String, SpecificRecord> sendResult = mock(SendResult.class);
        org.apache.kafka.clients.producer.RecordMetadata metadata = new org.apache.kafka.clients.producer.RecordMetadata(
                new org.apache.kafka.common.TopicPartition("topic", 0), 0, 0, 0, 0, 0);
        when(sendResult.getRecordMetadata()).thenReturn(metadata);
        future.complete(sendResult);

        verify(kafkaTemplate).send(AppConstants.KAFKA_TOPIC_NOTIFICATION_EMAIL, "user@test.com", event);
    }

    @SuppressWarnings("unchecked")
    @Test
    void sendNotificationEvent_whenFailure_logsError() {
        EmailNotificationRequest request = new EmailNotificationRequest("user@test.com", "Subject", "template",
                Map.of("name", "Ana"));

        EmailNotificationEvent event = EmailNotificationEvent.newBuilder().setRecipient("user@test.com")
                .setSubject("Subject").setTemplateName("template").setVariables(Map.of("name", "Ana")).build();
        when(notificationEventMapper.toEmailNotificationEvent(request)).thenReturn(event);

        CompletableFuture<SendResult<String, SpecificRecord>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(eq(AppConstants.KAFKA_TOPIC_NOTIFICATION_EMAIL), eq("user@test.com"), any()))
                .thenReturn(future);

        adapter.sendNotificationEvent(request);

        future.completeExceptionally(new RuntimeException("Kafka is down"));

        verify(kafkaTemplate).send(AppConstants.KAFKA_TOPIC_NOTIFICATION_EMAIL, "user@test.com", event);
    }
}
