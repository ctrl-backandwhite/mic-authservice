package com.backandwhite.api.mapper;

import static com.backandwhite.provider.GroupProvider.ADMIN_DESCRIPTION;
import static com.backandwhite.provider.GroupProvider.ADMIN_ENABLED;
import static com.backandwhite.provider.GroupProvider.ADMIN_ID;
import static com.backandwhite.provider.GroupProvider.ADMIN_NAME;
import static com.backandwhite.provider.GroupProvider.ADMIN_UNIQUE_NAME;
import static com.backandwhite.provider.GroupProvider.adminGroup;
import static com.backandwhite.provider.GroupProvider.adminGroupDtoIn;
import static com.backandwhite.provider.GroupProvider.adminGroupDtoOut;
import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.api.dto.in.GroupDtoIn;
import com.backandwhite.api.dto.out.GroupDtoOut;
import com.backandwhite.domain.model.Group;
import com.backandwhite.domain.model.Role;
import com.backandwhite.util.MapperTestUtils;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class GroupDtoMapperTest {

    private GroupDtoMapper mapper;

    @BeforeEach
    void setUp() {
        PermissionDtoMapper permissionDtoMapper = Mappers.getMapper(PermissionDtoMapper.class);
        RoleDtoMapper roleDtoMapper = Mappers.getMapper(RoleDtoMapper.class);
        MapperTestUtils.setField(roleDtoMapper, "permissionDtoMapper", permissionDtoMapper);
        mapper = Mappers.getMapper(GroupDtoMapper.class);
        MapperTestUtils.setField(mapper, "roleDtoMapper", roleDtoMapper);
        MapperTestUtils.setField(mapper, "permissionDtoMapper", permissionDtoMapper);
    }

    @Test
    void toDtoOut_mapsDomainToDtoOut() {
        Group model = adminGroup();

        GroupDtoOut result = mapper.toDtoOut(model);

        assertThat(result).usingRecursiveComparison().isEqualTo(adminGroupDtoOut(ADMIN_ID));
    }

    @Test
    void toDomain_mapsDtoInToDomain() {
        GroupDtoIn dtoIn = adminGroupDtoIn();

        Group result = mapper.toDomain(dtoIn);

        Group expected = Group.builder().name(ADMIN_NAME).uniqueName(ADMIN_UNIQUE_NAME).description(ADMIN_DESCRIPTION)
                .enabled(ADMIN_ENABLED).roles(List.of(Role.builder().id(ADMIN_ID).build())).build();

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toDtoOutList_mapsList() {
        List<GroupDtoOut> result = mapper.toDtoOutList(List.of(adminGroup()));

        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(adminGroupDtoOut(ADMIN_ID)));
    }
}
