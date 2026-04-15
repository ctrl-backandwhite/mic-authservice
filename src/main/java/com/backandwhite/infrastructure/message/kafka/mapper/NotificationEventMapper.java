package com.backandwhite.infrastructure.message.kafka.mapper;

import com.backandwhite.application.port.out.EmailNotificationRequest;
import com.backandwhite.core.kafka.avro.EmailNotificationEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationEventMapper {

    EmailNotificationEvent toEmailNotificationEvent(EmailNotificationRequest request);
}
