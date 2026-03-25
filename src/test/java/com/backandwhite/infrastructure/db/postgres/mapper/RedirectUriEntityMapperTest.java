package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.RedirectUri;
import com.backandwhite.infrastructure.db.postgres.entity.RedirectUriEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.backandwhite.provider.RedirectUriProvider.redirectUri;
import static com.backandwhite.provider.RedirectUriProvider.redirectUriEntity;
import static org.assertj.core.api.Assertions.assertThat;

class RedirectUriEntityMapperTest {

    private final RedirectUriEntityMapper mapper = Mappers.getMapper(RedirectUriEntityMapper.class);

    @Test
    void toDomain_mapsEntityToDomain() {
        RedirectUriEntity entity = redirectUriEntity();

        RedirectUri result = mapper.toDomain(entity);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(redirectUri());
    }

    @Test
    void toEntity_mapsDomainToEntity() {
        RedirectUri model = redirectUri();

        RedirectUriEntity result = mapper.toEntity(model);

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt", "createdBy", "updatedBy")
                .isEqualTo(redirectUriEntity());
    }

    @Test
    void toDomainList_mapsEntitiesToDomainList() {
        List<RedirectUriEntity> entities = List.of(redirectUriEntity());

        List<RedirectUri> result = mapper.toDomainList(entities);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(redirectUri()));
    }
}
