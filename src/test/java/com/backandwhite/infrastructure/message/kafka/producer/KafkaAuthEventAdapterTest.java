package com.backandwhite.infrastructure.message.kafka.producer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backandwhite.common.constants.AppConstants;
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
class KafkaAuthEventAdapterTest {

    @Mock
    private KafkaTemplate<String, SpecificRecord> kafkaTemplate;

    @InjectMocks
    private KafkaAuthEventAdapter adapter;

    @SuppressWarnings("unchecked")
    @Test
    void publishCustomerRegistered_sendsToKafka() {
        CompletableFuture<SendResult<String, SpecificRecord>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(eq(AppConstants.KAFKA_TOPIC_CUSTOMER_REGISTERED), eq("user-1"), any()))
                .thenReturn(future);

        adapter.publishCustomerRegistered("user-1", "test@test.com", "John", "Doe");

        verify(kafkaTemplate).send(eq(AppConstants.KAFKA_TOPIC_CUSTOMER_REGISTERED), eq("user-1"),
                any(SpecificRecord.class));
    }
}
