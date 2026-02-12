package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.in.OauthClientDtoIn;
import com.backandwhite.api.dto.out.OauthClientDtoOut;
import com.backandwhite.domain.model.OauthClient;

import com.backandwhite.domain.model.Scope;
import org.mapstruct.Named;
import java.util.Collections;
import java.util.stream.Collectors;
import com.backandwhite.domain.model.RedirectUri;
import com.backandwhite.domain.model.GrantType;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        ScopeDtoMapper.class,
        RedirectUriDtoMapper.class,
        GrantTypeDtoMapper.class,
})
public interface OauthClientDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "clientSecret", source = "clientSecret")
    @Mapping(target = "scopes", source = "scopes")
    @Mapping(target = "redirectUris", source = "redirectUris")
    @Mapping(target = "grantTypes", source = "grantTypes")
    OauthClientDtoOut toDtoOut(OauthClient model);

    @Mapping(target = "scopes", source = "scopeIds", qualifiedByName = "mapScopeIds")
    @Mapping(target = "redirectUris", source = "redirectUriIds", qualifiedByName = "mapRedirectUriIds")
    @Mapping(target = "grantTypes", source = "grantTypeIds", qualifiedByName = "mapGrantTypeIds")
    OauthClient toDomain(OauthClientDtoIn dtoIn);

    List<OauthClient> toDomainList(List<OauthClientDtoIn> dtos);

    List<OauthClientDtoOut> toDtoOutList(List<OauthClient> models);

    @Named("mapScopeIds")
    default List<Scope> mapScopeIds(List<Long> scopeIds) {
        if (scopeIds == null || scopeIds.isEmpty()) {
            return Collections.emptyList();
        }
        return scopeIds.stream()
            .map(id -> Scope.builder().id(id).build())
            .collect(Collectors.toList());
    }
    @Named("mapRedirectUriIds")
    default List<RedirectUri> mapRedirectUriIds(List<Long> redirectUriIds) {
        if (redirectUriIds == null || redirectUriIds.isEmpty()) {
            return Collections.emptyList();
        }
        return redirectUriIds.stream()
            .map(id -> RedirectUri.builder().id(id).build())
            .collect(Collectors.toList());
    }
    @Named("mapGrantTypeIds")
    default List<GrantType> mapGrantTypeIds(List<Long> grantTypeIds) {
        if (grantTypeIds == null || grantTypeIds.isEmpty()) {
            return Collections.emptyList();
        }
        return grantTypeIds.stream()
            .map(id -> GrantType.builder().id(id).build())
            .collect(Collectors.toList());
    }
}
