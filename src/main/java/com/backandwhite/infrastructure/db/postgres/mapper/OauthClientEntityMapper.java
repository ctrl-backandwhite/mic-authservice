package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.OauthClient;
import com.backandwhite.infrastructure.db.postgres.entity.OauthClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring", uses = {
    ScopeEntityMapper.class,
    RedirectUriEntityMapper.class,
    GrantTypeEntityMapper.class
})
public interface OauthClientEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "clientSecret", source = "clientSecret")
    @Mapping(target = "scopes", source = "scopes")
    @Mapping(target = "redirectUris", source = "redirectUris")
    @Mapping(target = "grantTypes", source = "grantTypes")
    OauthClient toDomain(OauthClientEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "clientSecret", source = "clientSecret")
    @Mapping(target = "scopes", source = "scopes")
    @Mapping(target = "redirectUris", source = "redirectUris")
    @Mapping(target = "grantTypes", source = "grantTypes")
    OauthClientEntity toEntity(OauthClient model);

    List<OauthClient> toDomainList(List<OauthClientEntity> entities);

    List<OauthClientEntity> toEntityList(List<OauthClient> models);
}
