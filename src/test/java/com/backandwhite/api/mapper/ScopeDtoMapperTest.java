package com.backandwhite.api.mapper;

import static com.backandwhite.provider.ScopeProvider.READ_ID;
import static com.backandwhite.provider.ScopeProvider.readScope;
import static com.backandwhite.provider.ScopeProvider.readScopeDtoIn;
import static com.backandwhite.provider.ScopeProvider.readScopeDtoOut;
import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.api.dto.in.ScopeDtoIn;
import com.backandwhite.api.dto.out.ScopeDtoOut;
import com.backandwhite.domain.model.Scope;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ScopeDtoMapperTest {

    private final ScopeDtoMapper mapper = Mappers.getMapper(ScopeDtoMapper.class);

    @Test
    void toDtoOut_mapsDomainToDtoOut() {
        Scope model = readScope();

        ScopeDtoOut result = mapper.toDtoOut(model);

        assertThat(result).usingRecursiveComparison().isEqualTo(readScopeDtoOut(READ_ID));
    }

    @Test
    void toDomain_mapsDtoInToDomain() {
        ScopeDtoIn dtoIn = readScopeDtoIn();

        Scope result = mapper.toDomain(dtoIn);

        assertThat(result).usingRecursiveComparison().ignoringFields("createdAt", "updatedAt", "createdBy", "updatedBy")
                .isEqualTo(readScope().withId(null));
    }

    @Test
    void toDtoOutList_mapsList() {
        List<ScopeDtoOut> result = mapper.toDtoOutList(List.of(readScope()));

        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(readScopeDtoOut(READ_ID)));
    }
}
