package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.User;
import com.backandwhite.infrastructure.db.postgres.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Field;
import java.util.List;

import static com.backandwhite.provider.GroupProvider.group;
import static com.backandwhite.provider.GroupProvider.groupEntity;
import static com.backandwhite.provider.RoleProvider.role;
import static com.backandwhite.provider.RoleProvider.roleEntity;
import static com.backandwhite.provider.ScopeProvider.readScope;
import static com.backandwhite.provider.ScopeProvider.scopeEntity;
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
import static com.backandwhite.provider.UserProvider.userEntity;
import static com.backandwhite.provider.AuditProvider.CREATED_AT;
import static com.backandwhite.provider.AuditProvider.CREATED_BY;
import static com.backandwhite.provider.AuditProvider.UPDATED_AT;
import static com.backandwhite.provider.AuditProvider.UPDATED_BY;
import static org.assertj.core.api.Assertions.assertThat;

class UserEntityMapperTest {

    private UserEntityMapper mapper;

    @BeforeEach
    void setUp() {
        ScopeEntityMapper scopeEntityMapper = Mappers.getMapper(ScopeEntityMapper.class);
        RoleEntityMapper roleEntityMapper = Mappers.getMapper(RoleEntityMapper.class);
        GroupEntityMapper groupEntityMapper = Mappers.getMapper(GroupEntityMapper.class);
        setField(groupEntityMapper, "roleEntityMapper", roleEntityMapper);

        mapper = Mappers.getMapper(UserEntityMapper.class);
        setField(mapper, "scopeEntityMapper", scopeEntityMapper);
        setField(mapper, "roleEntityMapper", roleEntityMapper);
        setField(mapper, "groupEntityMapper", groupEntityMapper);
    }

    @Test
    void toDomain_mapsEntityToDomain() {
        UserEntity entity = userEntity();

        User result = mapper.toDomain(entity);

        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(result.getUpdatedAt()).isEqualTo(UPDATED_AT);
        assertThat(result.getCreatedBy()).isEqualTo(CREATED_BY);
        assertThat(result.getUpdatedBy()).isEqualTo(UPDATED_BY);
        assertThat(result.getName()).isEqualTo(USER_NAME);
        assertThat(result.getLastName()).isEqualTo(USER_LAST_NAME);
        assertThat(result.getNickName()).isEqualTo(USER_NICK_NAME);
        assertThat(result.getEmail()).isEqualTo(USER_EMAIL);
        assertThat(result.getPassword()).isEqualTo(USER_PASSWORD);
        assertThat(result.getEnabled()).isEqualTo(USER_ENABLED);
        assertThat(result.getAccountNonExpired()).isEqualTo(USER_ACCOUNT_NON_EXPIRED);
        assertThat(result.getAccountNonLocked()).isEqualTo(USER_ACCOUNT_NON_LOCKED);
        assertThat(result.getCredentialsNonExpired()).isEqualTo(USER_CREDENTIALS_NON_EXPIRED);
        assertThat(result.getScopes()).hasSize(1);
        assertThat(result.getScopes().get(0))
                .usingRecursiveComparison()
                .isEqualTo(readScope());
        assertThat(result.getRoles()).hasSize(1);
        assertThat(result.getRoles().get(0))
                .usingRecursiveComparison()
                .isEqualTo(role());
        assertThat(result.getGroups()).hasSize(1);
        assertThat(result.getGroups().get(0))
                .usingRecursiveComparison()
                .isEqualTo(group());
    }

    @Test
    void toEntity_mapsDomainToEntity() {
        User model = User.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .lastName(USER_LAST_NAME)
                .nickName(USER_NICK_NAME)
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .enabled(USER_ENABLED)
                .accountNonExpired(USER_ACCOUNT_NON_EXPIRED)
                .accountNonLocked(USER_ACCOUNT_NON_LOCKED)
                .credentialsNonExpired(USER_CREDENTIALS_NON_EXPIRED)
                .scopes(List.of(readScope()))
                .roles(List.of(role()))
                .groups(List.of(group()))
                .build();

        UserEntity result = mapper.toEntity(model);

        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getCreatedAt()).isNull();
        assertThat(result.getUpdatedAt()).isNull();
        assertThat(result.getCreatedBy()).isNull();
        assertThat(result.getUpdatedBy()).isNull();
        assertThat(result.getName()).isEqualTo(USER_NAME);
        assertThat(result.getLastName()).isEqualTo(USER_LAST_NAME);
        assertThat(result.getNickName()).isEqualTo(USER_NICK_NAME);
        assertThat(result.getEmail()).isEqualTo(USER_EMAIL);
        assertThat(result.getPassword()).isEqualTo(USER_PASSWORD);
        assertThat(result.getEnabled()).isEqualTo(USER_ENABLED);
        assertThat(result.getAccountNonExpired()).isEqualTo(USER_ACCOUNT_NON_EXPIRED);
        assertThat(result.getAccountNonLocked()).isEqualTo(USER_ACCOUNT_NON_LOCKED);
        assertThat(result.getCredentialsNonExpired()).isEqualTo(USER_CREDENTIALS_NON_EXPIRED);
        assertThat(result.getScopes()).hasSize(1);
        assertThat(result.getScopes().get(0))
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .isEqualTo(scopeEntity());
        assertThat(result.getRoles()).hasSize(1);
        assertThat(result.getRoles().get(0))
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .isEqualTo(roleEntity());
        assertThat(result.getGroups()).hasSize(1);
        assertThat(result.getGroups().get(0))
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .isEqualTo(groupEntity());
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new IllegalStateException("Unable to set mapper dependency", exception);
        }
    }
}
