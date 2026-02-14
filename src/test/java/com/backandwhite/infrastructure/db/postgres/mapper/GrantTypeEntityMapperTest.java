package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.GrantType;
import com.backandwhite.infrastructure.db.postgres.entity.GrantTypeEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.backandwhite.provider.GrantTypeProvider.grantType;
import static com.backandwhite.provider.GrantTypeProvider.grantTypeEntity;
import static org.assertj.core.api.Assertions.assertThat;

class GrantTypeEntityMapperTest {

    private final GrantTypeEntityMapper mapper = Mappers.getMapper(GrantTypeEntityMapper.class);

    @Test
    void toDomain_mapsEntityToDomain() {
        GrantTypeEntity entity = grantTypeEntity();

        GrantType result = mapper.toDomain(entity);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(grantType());
    }

    @Test
    void toEntity_mapsDomainToEntity() {
        GrantType model = grantType();

        GrantTypeEntity result = mapper.toEntity(model);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(grantTypeEntity());
    }

    @Test
    void toDomainList_mapsEntitiesToDomainList() {
        List<GrantTypeEntity> entities = List.of(grantTypeEntity());

        List<GrantType> result = mapper.toDomainList(entities);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(grantType()));
    }
}
