package com.backandwhite.application.mapper;

import com.backandwhite.domain.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleUpdateMapper {

    @Mapping(target = "id", ignore = true)
    void updateFromModel(Role source, @MappingTarget Role target);
}
