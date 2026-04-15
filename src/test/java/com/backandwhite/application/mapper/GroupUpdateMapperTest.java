package com.backandwhite.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.domain.model.Group;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class GroupUpdateMapperTest {

    private GroupUpdateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(GroupUpdateMapper.class);
    }

    @Test
    void updateFromModel_copiesFieldsExceptIgnored() {
        Instant originalCreatedAt = Instant.parse("2026-01-01T00:00:00Z");
        Group source = Group.builder().id(99L).name("New").uniqueName("NEW").description("New desc").enabled(false)
                .createdAt(Instant.now()).build();
        Group target = Group.builder().id(1L).name("Old").uniqueName("OLD").description("Old desc").enabled(true)
                .createdAt(originalCreatedAt).build();

        mapper.updateFromModel(source, target);

        assertThat(target.getId()).isEqualTo(1L);
        assertThat(target.getName()).isEqualTo("New");
        assertThat(target.getUniqueName()).isEqualTo("NEW");
        assertThat(target.getDescription()).isEqualTo("New desc");
        assertThat(target.getEnabled()).isFalse();
        assertThat(target.getCreatedAt()).isEqualTo(originalCreatedAt);
    }
}
