package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.in.GroupDtoIn;
import com.backandwhite.api.dto.out.GroupDtoOut;
import com.backandwhite.domain.model.Group;
import com.backandwhite.domain.model.Permission;
import com.backandwhite.domain.model.Role;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {RoleDtoMapper.class, PermissionDtoMapper.class,})
public interface GroupDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "permissions", source = "permissions")
    GroupDtoOut toDtoOut(Group model);

    @Mapping(target = "roles", source = "roleIds", qualifiedByName = "mapRoleIds")
    @Mapping(target = "permissions", source = "permissionIds", qualifiedByName = "mapGroupPermissionIds")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Group toDomain(GroupDtoIn dtoIn);

    List<Group> toDomainList(List<GroupDtoIn> dtos);

    List<GroupDtoOut> toDtoOutList(List<Group> models);

    @Named("mapRoleIds")
    default List<Role> mapRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return roleIds.stream().map(id -> Role.builder().id(id).build()).collect(Collectors.toList());
    }

    @Named("mapGroupPermissionIds")
    default List<Permission> mapGroupPermissionIds(List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return Collections.emptyList();
        }
        return permissionIds.stream().map(id -> Permission.builder().id(id).build()).collect(Collectors.toList());
    }
}
