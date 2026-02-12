package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.Role;
import com.backandwhite.infrastructure.db.postgres.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring", uses = {
})
public interface RoleEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    Role toDomain(RoleEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    RoleEntity toEntity(Role model);

    List<Role> toDomainList(List<RoleEntity> entities);

    List<RoleEntity> toEntityList(List<Role> models);
}
