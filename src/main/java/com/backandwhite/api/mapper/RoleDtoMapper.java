package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.in.RoleDtoIn;
import com.backandwhite.api.dto.out.PermissionDtoOut;
import com.backandwhite.api.dto.out.RoleDtoOut;
import com.backandwhite.domain.model.Permission;
import com.backandwhite.domain.model.Role;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {
        PermissionDtoMapper.class
})
public interface RoleDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "permissions", source = "permissions")
    RoleDtoOut toDtoOut(Role model);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "permissions", source = "permissionIds", qualifiedByName = "mapPermissionIds")
    Role toDomain(RoleDtoIn dtoIn);

    List<Role> toDomainList(List<RoleDtoIn> dtos);

    List<RoleDtoOut> toDtoOutList(List<Role> models);

    @Named("mapPermissionIds")
    default List<Permission> mapPermissionIds(List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return Collections.emptyList();
        }
        return permissionIds.stream()
                .map(id -> Permission.builder().id(id).build())
                .collect(Collectors.toList());
    }
}
