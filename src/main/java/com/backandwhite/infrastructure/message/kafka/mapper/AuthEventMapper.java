package com.backandwhite.infrastructure.message.kafka.mapper;

import com.backandwhite.application.port.out.CustomerRegisteredRequest;
import com.backandwhite.core.kafka.avro.CustomerRegisteredEvent;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = Instant.class)
public interface AuthEventMapper {

    @Mapping(target = "timestamp", expression = "java(Instant.now().toString())")
    CustomerRegisteredEvent toCustomerRegisteredEvent(CustomerRegisteredRequest request);
}
