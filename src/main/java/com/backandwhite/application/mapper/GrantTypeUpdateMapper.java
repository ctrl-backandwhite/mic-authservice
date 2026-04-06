package com.backandwhite.application.mapper;

import com.backandwhite.domain.model.GrantType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GrantTypeUpdateMapper {

    @Mapping(target = "id", ignore = true)
    void updateFromModel(GrantType source, @MappingTarget GrantType target);
}
