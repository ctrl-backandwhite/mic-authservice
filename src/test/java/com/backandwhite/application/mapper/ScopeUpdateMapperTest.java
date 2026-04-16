package com.backandwhite.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.domain.model.Scope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ScopeUpdateMapperTest {

    private ScopeUpdateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ScopeUpdateMapper.class);
    }

    @Test
    void updateFromModel_copiesFieldsExceptId() {
        Scope source = Scope.builder().id(99L).name("New").uniqueName("NEW").description("New desc").enabled(false)
                .build();
        Scope target = Scope.builder().id(1L).name("Old").uniqueName("OLD").description("Old desc").enabled(true)
                .build();

        mapper.updateFromModel(source, target);

        assertThat(target.getId()).isEqualTo(1L);
        assertThat(target.getName()).isEqualTo("New");
        assertThat(target.getUniqueName()).isEqualTo("NEW");
        assertThat(target.getDescription()).isEqualTo("New desc");
        assertThat(target.getEnabled()).isFalse();
    }
}
