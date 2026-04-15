package com.backandwhite.api.mapper;

import static com.backandwhite.provider.PermissionProvider.PERMISSION_ID;
import static com.backandwhite.provider.PermissionProvider.permission;
import static com.backandwhite.provider.PermissionProvider.permissionDtoIn;
import static com.backandwhite.provider.PermissionProvider.permissionDtoOut;
import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.api.dto.in.PermissionDtoIn;
import com.backandwhite.api.dto.out.PermissionDtoOut;
import com.backandwhite.domain.model.Permission;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PermissionDtoMapperTest {

    private PermissionDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PermissionDtoMapper.class);
    }

    @Test
    void toDtoOut_mapsDomainToDtoOut() {
        Permission model = permission();

        PermissionDtoOut result = mapper.toDtoOut(model);

        assertThat(result).usingRecursiveComparison().isEqualTo(permissionDtoOut(PERMISSION_ID));
    }

    @Test
    void toDomain_mapsDtoInToDomain() {
        PermissionDtoIn dtoIn = permissionDtoIn();

        Permission result = mapper.toDomain(dtoIn);

        assertThat(result).usingRecursiveComparison().ignoringFields("createdAt", "updatedAt", "createdBy", "updatedBy")
                .isEqualTo(permission().withId(null));
    }

    @Test
    void toDtoOutList_mapsList() {
        List<PermissionDtoOut> result = mapper.toDtoOutList(List.of(permission()));

        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(permissionDtoOut(PERMISSION_ID)));
    }
}
