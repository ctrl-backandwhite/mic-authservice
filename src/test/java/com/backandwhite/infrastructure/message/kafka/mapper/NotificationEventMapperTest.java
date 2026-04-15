package com.backandwhite.infrastructure.message.kafka.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.application.port.out.EmailNotificationRequest;
import com.backandwhite.core.kafka.avro.EmailNotificationEvent;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class NotificationEventMapperTest {

    private final NotificationEventMapper mapper = Mappers.getMapper(NotificationEventMapper.class);

    @Test
    void toEmailNotificationEvent_mapsAllFields() {
        Map<String, String> variables = Map.of("name", "Ana", "code", "123456");
        EmailNotificationRequest request = new EmailNotificationRequest("ana@test.com", "Test Subject", "test-template",
                variables);

        EmailNotificationEvent event = mapper.toEmailNotificationEvent(request);

        assertThat(event.getRecipient()).isEqualTo("ana@test.com");
        assertThat(event.getSubject()).isEqualTo("Test Subject");
        assertThat(event.getTemplateName()).isEqualTo("test-template");
        assertThat(event.getVariables()).containsEntry("name", "Ana");
        assertThat(event.getVariables()).containsEntry("code", "123456");
    }

    @Test
    void toEmailNotificationEvent_handlesNullOptionalFields() {
        EmailNotificationRequest request = new EmailNotificationRequest("user@test.com", null, null, null);

        EmailNotificationEvent event = mapper.toEmailNotificationEvent(request);

        assertThat(event.getRecipient()).isEqualTo("user@test.com");
        assertThat(event.getSubject()).isNull();
        assertThat(event.getTemplateName()).isNull();
        assertThat(event.getVariables()).isNull();
    }
}
