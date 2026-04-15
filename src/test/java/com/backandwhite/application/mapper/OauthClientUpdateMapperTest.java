package com.backandwhite.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.domain.model.OauthClient;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class OauthClientUpdateMapperTest {

    private OauthClientUpdateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(OauthClientUpdateMapper.class);
    }

    @Test
    void updateFromModel_copiesFieldsExceptIgnored() {
        Instant originalCreatedAt = Instant.parse("2026-01-01T00:00:00Z");
        OauthClient source = OauthClient.builder().id(99L).clientId("new-client").clientSecret("new-secret")
                .createdAt(Instant.now()).build();
        OauthClient target = OauthClient.builder().id(1L).clientId("old-client").clientSecret("old-secret")
                .createdAt(originalCreatedAt).build();

        mapper.updateFromModel(source, target);

        assertThat(target.getId()).isEqualTo(1L);
        assertThat(target.getClientId()).isEqualTo("new-client");
        assertThat(target.getClientSecret()).isEqualTo("new-secret");
        assertThat(target.getCreatedAt()).isEqualTo(originalCreatedAt);
    }
}
