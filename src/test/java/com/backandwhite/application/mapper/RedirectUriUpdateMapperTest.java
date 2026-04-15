package com.backandwhite.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.domain.model.RedirectUri;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class RedirectUriUpdateMapperTest {

    private RedirectUriUpdateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(RedirectUriUpdateMapper.class);
    }

    @Test
    void updateFromModel_copiesFieldsExceptId() {
        RedirectUri source = RedirectUri.builder().id(99L).name("New").value("https://new.com/callback").enabled(false)
                .build();
        RedirectUri target = RedirectUri.builder().id(1L).name("Old").value("https://old.com/callback").enabled(true)
                .build();

        mapper.updateFromModel(source, target);

        assertThat(target.getId()).isEqualTo(1L);
        assertThat(target.getName()).isEqualTo("New");
        assertThat(target.getValue()).isEqualTo("https://new.com/callback");
        assertThat(target.getEnabled()).isFalse();
    }
}
