package com.backandwhite.infrastructure.db.postgres.mapper;

import static com.backandwhite.provider.RoleProvider.role;
import static com.backandwhite.provider.RoleProvider.roleEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.domain.model.Role;
import com.backandwhite.infrastructure.db.postgres.entity.RoleEntity;
import com.backandwhite.util.MapperTestUtils;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class RoleEntityMapperTest {

    private RoleEntityMapper mapper;

    @BeforeEach
    void setUp() {
        PermissionEntityMapper permissionEntityMapper = Mappers.getMapper(PermissionEntityMapper.class);
        mapper = Mappers.getMapper(RoleEntityMapper.class);
        MapperTestUtils.setField(mapper, "permissionEntityMapper", permissionEntityMapper);
    }

    @Test
    void toDomain_mapsEntityToDomain() {
        RoleEntity entity = roleEntity();

        Role result = mapper.toDomain(entity);

        assertThat(result).usingRecursiveComparison().isEqualTo(role());
    }

    @Test
    void toEntity_mapsDomainToEntity() {
        Role model = role();

        RoleEntity result = mapper.toEntity(model);

        assertThat(result).usingRecursiveComparison().ignoringFields("createdAt", "updatedAt", "createdBy", "updatedBy")
                .isEqualTo(roleEntity());
    }

    @Test
    void toDomainList_mapsEntitiesToDomainList() {
        List<RoleEntity> entities = List.of(roleEntity());

        List<Role> result = mapper.toDomainList(entities);

        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(role()));
    }
}
