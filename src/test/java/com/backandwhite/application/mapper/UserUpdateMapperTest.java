package com.backandwhite.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserUpdateMapperTest {

    private UserUpdateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserUpdateMapper.class);
    }

    @Test
    void updateFromModel_copiesFieldsExceptIgnored() {
        User source = User.builder().id(99L).name("New").lastName("NewLast").nickName("new.nick").email("new@test.com")
                .password("new-pass").enabled(false).build();
        User target = User.builder().id(1L).name("Old").lastName("OldLast").nickName("old.nick").email("old@test.com")
                .password("old-pass").enabled(true).build();

        mapper.updateFromModel(source, target);

        assertThat(target.getId()).isEqualTo(1L);
        assertThat(target.getPassword()).isEqualTo("old-pass");
        assertThat(target.getName()).isEqualTo("New");
        assertThat(target.getLastName()).isEqualTo("NewLast");
        assertThat(target.getEmail()).isEqualTo("new@test.com");
        assertThat(target.getEnabled()).isFalse();
    }
}
