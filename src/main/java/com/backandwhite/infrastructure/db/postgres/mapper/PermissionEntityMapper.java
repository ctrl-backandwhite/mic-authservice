package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.Permission;
import com.backandwhite.infrastructure.db.postgres.entity.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    Permission toDomain(PermissionEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    PermissionEntity toEntity(Permission model);

    List<Permission> toDomainList(List<PermissionEntity> entities);

    List<PermissionEntity> toEntityList(List<Permission> models);
}
