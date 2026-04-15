package com.backandwhite.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.domain.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class RoleUpdateMapperTest {

    private RoleUpdateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(RoleUpdateMapper.class);
    }

    @Test
    void updateFromModel_copiesFieldsExceptId() {
        Role source = Role.builder().id(99L).name("New").uniqueName("NEW").description("New desc")
                .enabled(false).build();
        Role target = Role.builder().id(1L).name("Old").uniqueName("OLD").description("Old desc")
                .enabled(true).build();

        mapper.updateFromModel(source, target);

        assertThat(target.getId()).isEqualTo(1L);
        assertThat(target.getName()).isEqualTo("New");
        assertThat(target.getUniqueName()).isEqualTo("NEW");
        assertThat(target.getDescription()).isEqualTo("New desc");
        assertThat(target.getEnabled()).isFalse();
    }
}
