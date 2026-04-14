package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.GrantType;
import com.backandwhite.infrastructure.db.postgres.entity.GrantTypeEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface GrantTypeEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "enabled", source = "enabled")
    GrantType toDomain(GrantTypeEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "value", source = "value")
    @Mapping(target = "enabled", source = "enabled")
    GrantTypeEntity toEntity(GrantType model);

    List<GrantType> toDomainList(List<GrantTypeEntity> entities);

    List<GrantTypeEntity> toEntityList(List<GrantType> models);
}
