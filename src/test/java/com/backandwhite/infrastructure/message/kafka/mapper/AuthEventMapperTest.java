package com.backandwhite.infrastructure.message.kafka.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.application.port.out.CustomerRegisteredRequest;
import com.backandwhite.core.kafka.avro.CustomerRegisteredEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AuthEventMapperTest {

    private final AuthEventMapper mapper = Mappers.getMapper(AuthEventMapper.class);

    @Test
    void toCustomerRegisteredEvent_mapsAllFields() {
        CustomerRegisteredRequest request = new CustomerRegisteredRequest("user-1", "ana@test.com", "Ana", "Lopez");

        CustomerRegisteredEvent event = mapper.toCustomerRegisteredEvent(request);

        assertThat(event.getUserId()).isEqualTo("user-1");
        assertThat(event.getEmail()).isEqualTo("ana@test.com");
        assertThat(event.getFirstName()).isEqualTo("Ana");
        assertThat(event.getLastName()).isEqualTo("Lopez");
        assertThat(event.getTimestamp()).isNotNull().isNotBlank();
    }
}
