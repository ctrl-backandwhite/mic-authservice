package com.backandwhite.api.mapper;

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

import com.backandwhite.api.dto.in.UserDtoIn;
import com.backandwhite.api.dto.out.UserDtoOut;
import com.backandwhite.domain.model.Group;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.model.User;
import com.backandwhite.util.MapperTestUtils;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserDtoMapperTest {

    private UserDtoMapper mapper;

    @BeforeEach
    void setUp() {
        PermissionDtoMapper permissionDtoMapper = Mappers.getMapper(PermissionDtoMapper.class);
        RoleDtoMapper roleDtoMapper = Mappers.getMapper(RoleDtoMapper.class);
        MapperTestUtils.setField(roleDtoMapper, "permissionDtoMapper", permissionDtoMapper);
        GroupDtoMapper groupDtoMapper = Mappers.getMapper(GroupDtoMapper.class);
        MapperTestUtils.setField(groupDtoMapper, "roleDtoMapper", roleDtoMapper);
        MapperTestUtils.setField(groupDtoMapper, "permissionDtoMapper", permissionDtoMapper);

        mapper = Mappers.getMapper(UserDtoMapper.class);
        MapperTestUtils.setField(mapper, "roleDtoMapper", roleDtoMapper);
        MapperTestUtils.setField(mapper, "groupDtoMapper", groupDtoMapper);
    }

    @Test
    void toDtoOut_mapsDomainToDtoOut() {
        User model = user();

        UserDtoOut result = mapper.toDtoOut(model);

        assertThat(result).usingRecursiveComparison().ignoringFields("password").isEqualTo(userDtoOut(USER_ID));
    }

    @Test
    void toDomain_mapsDtoInToDomain() {
        UserDtoIn dtoIn = userDtoIn();

        User result = mapper.toDomain(dtoIn);

        User expected = User.builder().name(USER_NAME).lastName(USER_LAST_NAME).nickName(USER_NICK_NAME)
                .email(USER_EMAIL).password(USER_PASSWORD).enabled(USER_ENABLED)
                .accountNonExpired(USER_ACCOUNT_NON_EXPIRED).accountNonLocked(USER_ACCOUNT_NON_LOCKED)
                .credentialsNonExpired(USER_CREDENTIALS_NON_EXPIRED)
                .roles(List.of(Role.builder().id(com.backandwhite.provider.RoleProvider.ADMIN_ID).build()))
                .groups(List.of(Group.builder().id(com.backandwhite.provider.GroupProvider.ADMIN_ID).build())).build();

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toDtoOutList_mapsList() {
        List<UserDtoOut> result = mapper.toDtoOutList(List.of(user()));

        assertThat(result).usingRecursiveComparison().ignoringFields("password")
                .isEqualTo(List.of(userDtoOut(USER_ID)));
    }

    @Test
    void mapUserRoleIds_nullReturnsEmptyList() {
        assertThat(mapper.mapUserRoleIds(null)).isEmpty();
    }

    @Test
    void mapUserRoleIds_emptyReturnsEmptyList() {
        assertThat(mapper.mapUserRoleIds(Collections.emptyList())).isEmpty();
    }

    @Test
    void mapUserRoleIds_withIdsReturnRoles() {
        List<Role> result = mapper.mapUserRoleIds(List.of(1L, 2L));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void mapUserGroupIds_nullReturnsEmptyList() {
        assertThat(mapper.mapUserGroupIds(null)).isEmpty();
    }

    @Test
    void mapUserGroupIds_emptyReturnsEmptyList() {
        assertThat(mapper.mapUserGroupIds(Collections.emptyList())).isEmpty();
    }

    @Test
    void mapUserGroupIds_withIdsReturnGroups() {
        List<Group> result = mapper.mapUserGroupIds(List.of(1L, 2L));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void normalizeEmail_null_returnsNull() {
        assertThat(mapper.normalizeEmail(null)).isNull();
    }

    @Test
    void normalizeEmail_mixedCase_returnsTrimmedLowercase() {
        assertThat(mapper.normalizeEmail("  USER@TEST.COM  ")).isEqualTo("user@test.com");
    }

    @Test
    void normalizeNickName_null_returnsNull() {
        assertThat(mapper.normalizeNickName(null)).isNull();
    }

    @Test
    void normalizeNickName_mixedCase_returnsTrimmedLowercase() {
        assertThat(mapper.normalizeNickName("  AnaXX  ")).isEqualTo("anaxx");
    }
}
