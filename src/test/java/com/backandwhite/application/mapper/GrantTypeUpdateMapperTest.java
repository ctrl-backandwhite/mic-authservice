package com.backandwhite.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.domain.model.GrantType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class GrantTypeUpdateMapperTest {

    private GrantTypeUpdateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(GrantTypeUpdateMapper.class);
    }

    @Test
    void updateFromModel_copiesFieldsExceptId() {
        GrantType source = GrantType.builder().id(99L).value("new_value").enabled(false).build();
        GrantType target = GrantType.builder().id(1L).value("old_value").enabled(true).build();

        mapper.updateFromModel(source, target);

        assertThat(target.getId()).isEqualTo(1L);
        assertThat(target.getValue()).isEqualTo("new_value");
        assertThat(target.getEnabled()).isFalse();
    }
}
