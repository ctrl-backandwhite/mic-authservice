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
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "enabled", source = "enabled")
    GrantTypeDtoOut toDtoOut(GrantType model);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    GrantType toDomain(GrantTypeDtoIn dtoIn);

    List<GrantType> toDomainList(List<GrantTypeDtoIn> dtos);

    List<GrantTypeDtoOut> toDtoOutList(List<GrantType> models);

}
