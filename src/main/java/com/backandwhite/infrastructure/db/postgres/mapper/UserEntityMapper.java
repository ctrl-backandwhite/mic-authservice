package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.User;
import com.backandwhite.infrastructure.db.postgres.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        ScopeEntityMapper.class,
        RoleEntityMapper.class,
        GroupEntityMapper.class
})
public interface UserEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "nickName", source = "nickName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "accountNonExpired", source = "accountNonExpired")
    @Mapping(target = "accountNonLocked", source = "accountNonLocked")
    @Mapping(target = "credentialsNonExpired", source = "credentialsNonExpired")
    @Mapping(target = "scopes", source = "scopes")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "groups", source = "groups")
    User toDomain(UserEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "nickName", source = "nickName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "accountNonExpired", source = "accountNonExpired")
    @Mapping(target = "accountNonLocked", source = "accountNonLocked")
    @Mapping(target = "credentialsNonExpired", source = "credentialsNonExpired")
    @Mapping(target = "scopes", source = "scopes")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "groups", source = "groups")
    UserEntity toEntity(User model);

    List<User> toDomainList(List<UserEntity> entities);

    List<UserEntity> toEntityList(List<User> models);
}
