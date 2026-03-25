package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.Role;
import com.backandwhite.infrastructure.db.postgres.entity.RoleEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.backandwhite.provider.RoleProvider.role;
import static com.backandwhite.provider.RoleProvider.roleEntity;
import static org.assertj.core.api.Assertions.assertThat;

class RoleEntityMapperTest {

    private final RoleEntityMapper mapper = Mappers.getMapper(RoleEntityMapper.class);

    @Test
    void toDomain_mapsEntityToDomain() {
        RoleEntity entity = roleEntity();

        Role result = mapper.toDomain(entity);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(role());
    }

    @Test
    void toEntity_mapsDomainToEntity() {
        Role model = role();

        RoleEntity result = mapper.toEntity(model);

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt", "createdBy", "updatedBy")
                .isEqualTo(roleEntity());
    }

    @Test
    void toDomainList_mapsEntitiesToDomainList() {
        List<RoleEntity> entities = List.of(roleEntity());

        List<Role> result = mapper.toDomainList(entities);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(role()));
    }
}
