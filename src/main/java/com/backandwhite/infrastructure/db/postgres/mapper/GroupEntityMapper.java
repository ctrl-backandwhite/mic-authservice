package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.Group;
import com.backandwhite.infrastructure.db.postgres.entity.GroupEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleEntityMapper.class, PermissionEntityMapper.class})
public interface GroupEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "permissions", source = "permissions")
    Group toDomain(GroupEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "uniqueName", source = "uniqueName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "permissions", source = "permissions")
    @Mapping(target = "users", ignore = true)
    GroupEntity toEntity(Group model);

    List<Group> toDomainList(List<GroupEntity> entities);

    List<GroupEntity> toEntityList(List<Group> models);
}
