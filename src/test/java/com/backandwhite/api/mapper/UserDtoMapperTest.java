package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.in.UserDtoIn;
import com.backandwhite.api.dto.out.UserDtoOut;
import com.backandwhite.domain.model.Group;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.model.Scope;
import com.backandwhite.domain.model.User;
import com.backandwhite.util.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.backandwhite.provider.ScopeProvider.READ_ID;
import static com.backandwhite.provider.UserProvider.USER_ACCOUNT_NON_EXPIRED;
import static com.backandwhite.provider.UserProvider.USER_ACCOUNT_NON_LOCKED;
import static com.backandwhite.provider.UserProvider.USER_CREDENTIALS_NON_EXPIRED;
import static com.backandwhite.provider.UserProvider.USER_EMAIL;
import static com.backandwhite.provider.UserProvider.USER_ENABLED;
import static com.backandwhite.provider.UserProvider.USER_ID;
import static com.backandwhite.provider.UserProvider.USER_LAST_NAME;
import static com.backandwhite.provider.UserProvider.USER_NAME;
import static com.backandwhite.provider.UserProvider.USER_NICK_NAME;
import static com.backandwhite.provider.UserProvider.USER_PASSWORD;
import static com.backandwhite.provider.UserProvider.user;
import static com.backandwhite.provider.UserProvider.userDtoIn;
import static com.backandwhite.provider.UserProvider.userDtoOut;
import static org.assertj.core.api.Assertions.assertThat;

class UserDtoMapperTest {

    private UserDtoMapper mapper;

    @BeforeEach
    void setUp() {
        ScopeDtoMapper scopeDtoMapper = Mappers.getMapper(ScopeDtoMapper.class);
        RoleDtoMapper roleDtoMapper = Mappers.getMapper(RoleDtoMapper.class);
        GroupDtoMapper groupDtoMapper = Mappers.getMapper(GroupDtoMapper.class);
        MapperTestUtils.setField(groupDtoMapper, "roleDtoMapper", roleDtoMapper);

        mapper = Mappers.getMapper(UserDtoMapper.class);
        MapperTestUtils.setField(mapper, "scopeDtoMapper", scopeDtoMapper);
        MapperTestUtils.setField(mapper, "roleDtoMapper", roleDtoMapper);
        MapperTestUtils.setField(mapper, "groupDtoMapper", groupDtoMapper);
    }

    @Test
    void toDtoOut_mapsDomainToDtoOut() {
        User model = user();

        UserDtoOut result = mapper.toDtoOut(model);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(userDtoOut(USER_ID));
    }

    @Test
    void toDomain_mapsDtoInToDomain() {
        UserDtoIn dtoIn = userDtoIn();

        User result = mapper.toDomain(dtoIn);

        User expected = User.builder()
                .name(USER_NAME)
                .lastName(USER_LAST_NAME)
                .nickName(USER_NICK_NAME)
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .enabled(USER_ENABLED)
                .accountNonExpired(USER_ACCOUNT_NON_EXPIRED)
                .accountNonLocked(USER_ACCOUNT_NON_LOCKED)
                .credentialsNonExpired(USER_CREDENTIALS_NON_EXPIRED)
                .scopes(List.of(Scope.builder().id(READ_ID).build()))
                .roles(List.of(Role.builder().id(com.backandwhite.provider.RoleProvider.ADMIN_ID).build()))
                .groups(List.of(Group.builder().id(com.backandwhite.provider.GroupProvider.ADMIN_ID).build()))
                .build();

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void toDtoOutList_mapsList() {
        List<UserDtoOut> result = mapper.toDtoOutList(List.of(user()));

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(userDtoOut(USER_ID)));
    }
}
