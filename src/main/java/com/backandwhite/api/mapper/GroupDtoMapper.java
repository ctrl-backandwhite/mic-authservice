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
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "roles", source = "roles")
    GroupDtoOut toDtoOut(Group model);

    @Mapping(target = "roles", source = "roleIds", qualifiedByName = "mapRoleIds")
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
