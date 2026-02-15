package com.backandwhite.provider;

import com.backandwhite.api.dto.in.UserDtoIn;
import com.backandwhite.api.dto.out.UserDtoOut;
import com.backandwhite.domain.model.User;
import com.backandwhite.infrastructure.db.postgres.entity.UserEntity;

import java.util.List;

public final class UserProvider {

    public static final Long USER_ID = 1L;
    public static final String USER_NAME = "Ana";
    public static final String USER_LAST_NAME = "Lopez";
    public static final String USER_NICK_NAME = "ana.lopez";
    public static final String USER_EMAIL = "ana.lopez@example.com";
    public static final String USER_PASSWORD = "secret";
    public static final Boolean USER_ENABLED = true;
    public static final Boolean USER_ACCOUNT_NON_EXPIRED = true;
    public static final Boolean USER_ACCOUNT_NON_LOCKED = true;
    public static final Boolean USER_CREDENTIALS_NON_EXPIRED = true;

    public static final Long OTHER_USER_ID = 2L;
    public static final String OTHER_USER_NAME = "Bruno";
    public static final String OTHER_USER_LAST_NAME = "Diaz";
    public static final String OTHER_USER_NICK_NAME = "bruno.diaz";
    public static final String OTHER_USER_EMAIL = "bruno.diaz@example.com";
    public static final String OTHER_USER_PASSWORD = "secret-2";
    public static final Boolean OTHER_USER_ENABLED = false;
    public static final Boolean OTHER_USER_ACCOUNT_NON_EXPIRED = false;
    public static final Boolean OTHER_USER_ACCOUNT_NON_LOCKED = false;
    public static final Boolean OTHER_USER_CREDENTIALS_NON_EXPIRED = false;

    private UserProvider() {
        // Utility class.
    }

    public static User user() {
        return User.builder()
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
                .scopes(List.of(ScopeProvider.readScope()))
                .roles(List.of(RoleProvider.adminRole()))
                .groups(List.of(GroupProvider.adminGroup()))
                .build();
    }

    public static User otherUser() {
        return User.builder()
                .id(OTHER_USER_ID)
                .name(OTHER_USER_NAME)
                .lastName(OTHER_USER_LAST_NAME)
                .nickName(OTHER_USER_NICK_NAME)
                .email(OTHER_USER_EMAIL)
                .password(OTHER_USER_PASSWORD)
                .enabled(OTHER_USER_ENABLED)
                .accountNonExpired(OTHER_USER_ACCOUNT_NON_EXPIRED)
                .accountNonLocked(OTHER_USER_ACCOUNT_NON_LOCKED)
                .credentialsNonExpired(OTHER_USER_CREDENTIALS_NON_EXPIRED)
                .scopes(List.of(ScopeProvider.writeScope()))
                .roles(List.of(RoleProvider.userRole()))
                .groups(List.of(GroupProvider.userGroup()))
                .build();
    }

    public static UserEntity userEntity() {
        return UserEntity.builder()
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
                .scopes(List.of(ScopeProvider.scopeEntity()))
                .roles(List.of(RoleProvider.roleEntity()))
                .groups(List.of(GroupProvider.groupEntity()))
                .build();
    }

    public static UserEntity otherUserEntity() {
        return UserEntity.builder()
                .id(OTHER_USER_ID)
                .name(OTHER_USER_NAME)
                .lastName(OTHER_USER_LAST_NAME)
                .nickName(OTHER_USER_NICK_NAME)
                .email(OTHER_USER_EMAIL)
                .password(OTHER_USER_PASSWORD)
                .enabled(OTHER_USER_ENABLED)
                .accountNonExpired(OTHER_USER_ACCOUNT_NON_EXPIRED)
                .accountNonLocked(OTHER_USER_ACCOUNT_NON_LOCKED)
                .credentialsNonExpired(OTHER_USER_CREDENTIALS_NON_EXPIRED)
                .scopes(List.of(ScopeProvider.writeScopeEntity()))
                .roles(List.of(RoleProvider.userRoleEntity()))
                .groups(List.of(GroupProvider.userGroupEntity()))
                .build();
    }

    public static UserDtoIn userDtoIn() {
        return UserDtoIn.builder()
                .name(USER_NAME)
                .lastName(USER_LAST_NAME)
                .nickName(USER_NICK_NAME)
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .confirmPassword(USER_PASSWORD)
                .enabled(USER_ENABLED)
                .accountNonExpired(USER_ACCOUNT_NON_EXPIRED)
                .accountNonLocked(USER_ACCOUNT_NON_LOCKED)
                .credentialsNonExpired(USER_CREDENTIALS_NON_EXPIRED)
                .scopeIds(List.of(ScopeProvider.READ_ID))
                .roleIds(List.of(RoleProvider.ADMIN_ID))
                .groupIds(List.of(GroupProvider.ADMIN_ID))
                .build();
    }

    public static UserDtoIn otherUserDtoIn() {
        return UserDtoIn.builder()
                .name(OTHER_USER_NAME)
                .lastName(OTHER_USER_LAST_NAME)
                .nickName(OTHER_USER_NICK_NAME)
                .email(OTHER_USER_EMAIL)
                .password(OTHER_USER_PASSWORD)
                .confirmPassword(OTHER_USER_PASSWORD)
                .enabled(OTHER_USER_ENABLED)
                .accountNonExpired(OTHER_USER_ACCOUNT_NON_EXPIRED)
                .accountNonLocked(OTHER_USER_ACCOUNT_NON_LOCKED)
                .credentialsNonExpired(OTHER_USER_CREDENTIALS_NON_EXPIRED)
                .scopeIds(List.of(ScopeProvider.WRITE_ID))
                .roleIds(List.of(RoleProvider.USER_ID))
                .groupIds(List.of(GroupProvider.USER_ID))
                .build();
    }

    public static UserDtoOut userDtoOut(Long id) {
        return UserDtoOut.builder()
                .id(id)
                .name(USER_NAME)
                .lastName(USER_LAST_NAME)
                .nickName(USER_NICK_NAME)
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .enabled(USER_ENABLED)
                .accountNonExpired(USER_ACCOUNT_NON_EXPIRED)
                .accountNonLocked(USER_ACCOUNT_NON_LOCKED)
                .credentialsNonExpired(USER_CREDENTIALS_NON_EXPIRED)
                .scopes(List.of(ScopeProvider.readScopeDtoOut(null)))
                .roles(List.of(RoleProvider.adminRoleDtoOut(null)))
                .groups(List.of(GroupProvider.adminGroupDtoOut(null)))
                .build();
    }

    public static UserDtoOut otherUserDtoOut(Long id) {
        return UserDtoOut.builder()
                .id(id)
                .name(OTHER_USER_NAME)
                .lastName(OTHER_USER_LAST_NAME)
                .nickName(OTHER_USER_NICK_NAME)
                .email(OTHER_USER_EMAIL)
                .password(OTHER_USER_PASSWORD)
                .enabled(OTHER_USER_ENABLED)
                .accountNonExpired(OTHER_USER_ACCOUNT_NON_EXPIRED)
                .accountNonLocked(OTHER_USER_ACCOUNT_NON_LOCKED)
                .credentialsNonExpired(OTHER_USER_CREDENTIALS_NON_EXPIRED)
                .scopes(List.of(ScopeProvider.writeScopeDtoOut(null)))
                .roles(List.of(RoleProvider.userRoleDtoOut(null)))
                .groups(List.of(GroupProvider.userGroupDtoOut(null)))
                .build();
    }
}
