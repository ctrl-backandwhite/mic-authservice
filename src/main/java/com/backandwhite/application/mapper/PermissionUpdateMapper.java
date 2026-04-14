package com.backandwhite.application.mapper;

import com.backandwhite.domain.model.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionUpdateMapper {

    @Mapping(target = "id", ignore = true)
    void updateFromModel(Permission source, @MappingTarget Permission target);
}
