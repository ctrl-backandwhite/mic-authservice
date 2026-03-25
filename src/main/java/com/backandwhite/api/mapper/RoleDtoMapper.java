package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.in.RoleDtoIn;
import com.backandwhite.api.dto.out.RoleDtoOut;
import com.backandwhite.domain.model.Role;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
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
    RoleDtoOut toDtoOut(Role model);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Role toDomain(RoleDtoIn dtoIn);

    List<Role> toDomainList(List<RoleDtoIn> dtos);

    List<RoleDtoOut> toDtoOutList(List<Role> models);

}
