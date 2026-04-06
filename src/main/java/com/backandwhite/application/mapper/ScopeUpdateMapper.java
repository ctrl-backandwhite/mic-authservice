package com.backandwhite.application.mapper;

import com.backandwhite.domain.model.Scope;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ScopeUpdateMapper {

    @Mapping(target = "id", ignore = true)
    void updateFromModel(Scope source, @MappingTarget Scope target);
}
