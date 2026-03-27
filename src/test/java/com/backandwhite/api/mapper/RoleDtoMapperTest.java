package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.in.RoleDtoIn;
import com.backandwhite.api.dto.out.RoleDtoOut;
import com.backandwhite.domain.model.Role;
import com.backandwhite.util.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.backandwhite.provider.RoleProvider.ROLE_ID;
import static com.backandwhite.provider.RoleProvider.role;
import static com.backandwhite.provider.RoleProvider.supportRoleDtoIn;
import static com.backandwhite.provider.RoleProvider.supportRoleDtoOut;
import static org.assertj.core.api.Assertions.assertThat;

class RoleDtoMapperTest {

    private RoleDtoMapper mapper;

    @BeforeEach
    void setUp() {
        PermissionDtoMapper permissionDtoMapper = Mappers.getMapper(PermissionDtoMapper.class);
        mapper = Mappers.getMapper(RoleDtoMapper.class);
        MapperTestUtils.setField(mapper, "permissionDtoMapper", permissionDtoMapper);
    }

    @Test
    void toDtoOut_mapsDomainToDtoOut() {
        Role model = role();

        RoleDtoOut result = mapper.toDtoOut(model);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(supportRoleDtoOut(ROLE_ID));
    }

    @Test
    void toDomain_mapsDtoInToDomain() {
        RoleDtoIn dtoIn = supportRoleDtoIn();

        Role result = mapper.toDomain(dtoIn);

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt", "createdBy", "updatedBy")
                .isEqualTo(role().withId(null));
    }

    @Test
    void toDtoOutList_mapsList() {
        List<RoleDtoOut> result = mapper.toDtoOutList(List.of(role()));

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(supportRoleDtoOut(ROLE_ID)));
    }
}
