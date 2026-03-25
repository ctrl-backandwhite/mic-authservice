package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.in.ScopeDtoIn;
import com.backandwhite.api.dto.out.ScopeDtoOut;
import com.backandwhite.domain.model.Scope;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
})
public interface ScopeDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    ScopeDtoOut toDtoOut(Scope model);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Scope toDomain(ScopeDtoIn dtoIn);

    List<Scope> toDomainList(List<ScopeDtoIn> dtos);

    List<ScopeDtoOut> toDtoOutList(List<Scope> models);

}
