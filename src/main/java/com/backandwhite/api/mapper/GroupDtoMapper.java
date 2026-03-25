package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.in.GroupDtoIn;
import com.backandwhite.api.dto.out.GroupDtoOut;
import com.backandwhite.domain.model.Group;

import com.backandwhite.domain.model.Role;
import org.mapstruct.Named;
import java.util.Collections;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        RoleDtoMapper.class,
})
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
    GroupDtoOut toDtoOut(Group model);

    @Mapping(target = "roles", source = "roleIds", qualifiedByName = "mapRoleIds")
    @Mapping(target = "permissions", ignore = true)
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
        return roleIds.stream()
                .map(id -> Role.builder().id(id).build())
                .collect(Collectors.toList());
    }
}
