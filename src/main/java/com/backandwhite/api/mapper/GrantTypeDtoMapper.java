package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.in.GrantTypeDtoIn;
import com.backandwhite.api.dto.out.GrantTypeDtoOut;
import com.backandwhite.domain.model.GrantType;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
})
public interface GrantTypeDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "enabled", source = "enabled")
    GrantTypeDtoOut toDtoOut(GrantType model);

    GrantType toDomain(GrantTypeDtoIn dtoIn);

    List<GrantType> toDomainList(List<GrantTypeDtoIn> dtos);

    List<GrantTypeDtoOut> toDtoOutList(List<GrantType> models);

}
