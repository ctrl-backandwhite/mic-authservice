package com.backandwhite.api.mapper;

import static com.backandwhite.provider.GrantTypeProvider.GRANT_TYPE_ID;
import static com.backandwhite.provider.GrantTypeProvider.grantType;
import static com.backandwhite.provider.GrantTypeProvider.grantTypeDtoIn;
import static com.backandwhite.provider.GrantTypeProvider.grantTypeDtoOut;
import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.api.dto.in.GrantTypeDtoIn;
import com.backandwhite.api.dto.out.GrantTypeDtoOut;
import com.backandwhite.domain.model.GrantType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class GrantTypeDtoMapperTest {

    private final GrantTypeDtoMapper mapper = Mappers.getMapper(GrantTypeDtoMapper.class);

    @Test
    void toDtoOut_mapsDomainToDtoOut() {
        GrantType model = grantType();

        GrantTypeDtoOut result = mapper.toDtoOut(model);

        assertThat(result).usingRecursiveComparison().isEqualTo(grantTypeDtoOut(GRANT_TYPE_ID));
    }

    @Test
    void toDomain_mapsDtoInToDomain() {
        GrantTypeDtoIn dtoIn = grantTypeDtoIn();

        GrantType result = mapper.toDomain(dtoIn);

        assertThat(result).usingRecursiveComparison().ignoringFields("createdAt", "updatedAt", "createdBy", "updatedBy")
                .isEqualTo(grantType().withId(null));
    }

    @Test
    void toDtoOutList_mapsList() {
        List<GrantTypeDtoOut> result = mapper.toDtoOutList(List.of(grantType()));

        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(grantTypeDtoOut(GRANT_TYPE_ID)));
    }
}
