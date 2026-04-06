package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.in.UserDtoIn;
import com.backandwhite.api.dto.out.UserDtoOut;
import com.backandwhite.domain.model.User;

import org.mapstruct.Named;
import java.util.Collections;
import java.util.stream.Collectors;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.model.Group;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        RoleDtoMapper.class,
        GroupDtoMapper.class,
})
public interface UserDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "nickName", source = "nickName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "accountNonExpired", source = "accountNonExpired")
    @Mapping(target = "accountNonLocked", source = "accountNonLocked")
    @Mapping(target = "credentialsNonExpired", source = "credentialsNonExpired")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "groups", source = "groups")
    UserDtoOut toDtoOut(User model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "nickName", source = "nickName", qualifiedByName = "normalizeNickName")
    @Mapping(target = "email", source = "email", qualifiedByName = "normalizeEmail")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "roles", source = "roleIds", qualifiedByName = "mapUserRoleIds")
    @Mapping(target = "groups", source = "groupIds", qualifiedByName = "mapUserGroupIds")
    User toDomain(UserDtoIn dtoIn);

    List<User> toDomainList(List<UserDtoIn> dtos);

    List<UserDtoOut> toDtoOutList(List<User> models);

    @Named("normalizeEmail")
    default String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    @Named("normalizeNickName")
    default String normalizeNickName(String nickName) {
        return nickName == null ? null : nickName.trim().toLowerCase();
    }

    @Named("mapUserRoleIds")
    default List<Role> mapUserRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return roleIds.stream()
                .map(id -> Role.builder().id(id).build())
                .collect(Collectors.toList());
    }

    @Named("mapUserGroupIds")
    default List<Group> mapUserGroupIds(List<Long> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) {
            return Collections.emptyList();
        }
        return groupIds.stream()
                .map(id -> Group.builder().id(id).build())
                .collect(Collectors.toList());
    }
}
