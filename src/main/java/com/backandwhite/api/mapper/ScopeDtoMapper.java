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
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    ScopeDtoOut toDtoOut(Scope model);

    Scope toDomain(ScopeDtoIn dtoIn);

    List<Scope> toDomainList(List<ScopeDtoIn> dtos);

    List<ScopeDtoOut> toDtoOutList(List<Scope> models);

}
