package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.in.PermissionDtoIn;
import com.backandwhite.api.dto.out.PermissionDtoOut;
import com.backandwhite.domain.model.Permission;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface PermissionDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    PermissionDtoOut toDtoOut(Permission model);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Permission toDomain(PermissionDtoIn dtoIn);

    List<Permission> toDomainList(List<PermissionDtoIn> dtos);

    List<PermissionDtoOut> toDtoOutList(List<Permission> models);
}
