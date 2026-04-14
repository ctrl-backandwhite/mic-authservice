package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.RedirectUri;
import com.backandwhite.infrastructure.db.postgres.entity.RedirectUriEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface RedirectUriEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "enabled", source = "enabled")
    RedirectUri toDomain(RedirectUriEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "enabled", source = "enabled")
    RedirectUriEntity toEntity(RedirectUri model);

    List<RedirectUri> toDomainList(List<RedirectUriEntity> entities);

    List<RedirectUriEntity> toEntityList(List<RedirectUri> models);
}
