package com.backandwhite.application.mapper;

import com.backandwhite.domain.model.RedirectUri;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RedirectUriUpdateMapper {

    @Mapping(target = "id", ignore = true)
    void updateFromModel(RedirectUri source, @MappingTarget RedirectUri target);
}
