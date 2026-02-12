package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.Scope;
import com.backandwhite.infrastructure.db.postgres.entity.ScopeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring", uses = {
})
public interface ScopeEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    Scope toDomain(ScopeEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    ScopeEntity toEntity(Scope model);

    List<Scope> toDomainList(List<ScopeEntity> entities);

    List<ScopeEntity> toEntityList(List<Scope> models);
}
