package com.backandwhite.infrastructure.db.postgres.mapper;

import static com.backandwhite.provider.ScopeProvider.scope;
import static com.backandwhite.provider.ScopeProvider.scopeEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.domain.model.Scope;
import com.backandwhite.infrastructure.db.postgres.entity.ScopeEntity;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ScopeEntityMapperTest {

    private final ScopeEntityMapper mapper = Mappers.getMapper(ScopeEntityMapper.class);

    @Test
    void toDomain_mapsEntityToDomain() {
        ScopeEntity entity = scopeEntity();

        Scope result = mapper.toDomain(entity);

        assertThat(result).usingRecursiveComparison().isEqualTo(scope());
    }

    @Test
    void toEntity_mapsDomainToEntity() {
        Scope model = scope();

        ScopeEntity result = mapper.toEntity(model);

        assertThat(result).usingRecursiveComparison().ignoringFields("createdAt", "updatedAt", "createdBy", "updatedBy")
                .isEqualTo(scopeEntity());
    }

    @Test
    void toDomainList_mapsEntitiesToDomainList() {
        List<ScopeEntity> entities = List.of(scopeEntity());

        List<Scope> result = mapper.toDomainList(entities);

        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(scope()));
    }
}
