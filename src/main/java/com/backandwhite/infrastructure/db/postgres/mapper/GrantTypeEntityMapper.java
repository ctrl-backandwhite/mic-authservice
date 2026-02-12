package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.GrantType;
import com.backandwhite.infrastructure.db.postgres.entity.GrantTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring", uses = {
})
public interface GrantTypeEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "enabled", source = "enabled")
    GrantType toDomain(GrantTypeEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "enabled", source = "enabled")
    GrantTypeEntity toEntity(GrantType model);

    List<GrantType> toDomainList(List<GrantTypeEntity> entities);

    List<GrantTypeEntity> toEntityList(List<GrantType> models);
}
