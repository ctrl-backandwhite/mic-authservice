package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.in.RedirectUriDtoIn;
import com.backandwhite.api.dto.out.RedirectUriDtoOut;
import com.backandwhite.domain.model.RedirectUri;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
})
public interface RedirectUriDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "enabled", source = "enabled")
    RedirectUriDtoOut toDtoOut(RedirectUri model);

    RedirectUri toDomain(RedirectUriDtoIn dtoIn);

    List<RedirectUri> toDomainList(List<RedirectUriDtoIn> dtos);

    List<RedirectUriDtoOut> toDtoOutList(List<RedirectUri> models);

}
