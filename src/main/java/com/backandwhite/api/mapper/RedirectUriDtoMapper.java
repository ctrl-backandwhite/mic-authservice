package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.in.RedirectUriDtoIn;
import com.backandwhite.api.dto.out.RedirectUriDtoOut;
import com.backandwhite.domain.model.RedirectUri;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface RedirectUriDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "enabled", source = "enabled")
    RedirectUriDtoOut toDtoOut(RedirectUri model);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    RedirectUri toDomain(RedirectUriDtoIn dtoIn);

    List<RedirectUri> toDomainList(List<RedirectUriDtoIn> dtos);

    List<RedirectUriDtoOut> toDtoOutList(List<RedirectUri> models);

}
