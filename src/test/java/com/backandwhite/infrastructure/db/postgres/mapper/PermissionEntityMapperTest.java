package com.backandwhite.infrastructure.db.postgres.mapper;

import static com.backandwhite.provider.PermissionProvider.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.domain.model.Permission;
import com.backandwhite.infrastructure.db.postgres.entity.PermissionEntity;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PermissionEntityMapperTest {
    private final PermissionEntityMapper mapper = Mappers.getMapper(PermissionEntityMapper.class);

    @Test
    void toDomain_mapsEntityToDomain() {
        PermissionEntity entity = permissionEntity();
        Permission result = mapper.toDomain(entity);
        assertThat(result).usingRecursiveComparison().isEqualTo(permission());
    }

    @Test
    void toEntity_mapsDomainToEntity() {
        Permission model = permission();
        PermissionEntity result = mapper.toEntity(model);
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt", "createdBy", "updatedBy", "roles", "groups")
                .isEqualTo(permissionEntity());
    }

    @Test
    void toDomainList_mapsEntitiesToDomainList() {
        List<PermissionEntity> entities = List.of(permissionEntity());
        List<Permission> result = mapper.toDomainList(entities);
        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(permission()));
    }
}
