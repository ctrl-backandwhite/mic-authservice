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
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    RoleDtoOut toDtoOut(Role model);

    Role toDomain(RoleDtoIn dtoIn);

    List<Role> toDomainList(List<RoleDtoIn> dtos);

    List<RoleDtoOut> toDtoOutList(List<Role> models);

}
