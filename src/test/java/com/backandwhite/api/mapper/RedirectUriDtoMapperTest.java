package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.in.RedirectUriDtoIn;
import com.backandwhite.api.dto.out.RedirectUriDtoOut;
import com.backandwhite.domain.model.RedirectUri;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.backandwhite.provider.RedirectUriProvider.REDIRECT_URI_ID;
import static com.backandwhite.provider.RedirectUriProvider.redirectUri;
import static com.backandwhite.provider.RedirectUriProvider.redirectUriDtoIn;
import static com.backandwhite.provider.RedirectUriProvider.redirectUriDtoOut;
import static org.assertj.core.api.Assertions.assertThat;

class RedirectUriDtoMapperTest {

    private final RedirectUriDtoMapper mapper = Mappers.getMapper(RedirectUriDtoMapper.class);

    @Test
    void toDtoOut_mapsDomainToDtoOut() {
        RedirectUri model = redirectUri();

        RedirectUriDtoOut result = mapper.toDtoOut(model);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(redirectUriDtoOut(REDIRECT_URI_ID));
    }

    @Test
    void toDomain_mapsDtoInToDomain() {
        RedirectUriDtoIn dtoIn = redirectUriDtoIn();

        RedirectUri result = mapper.toDomain(dtoIn);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(redirectUri().withId(null));
    }

    @Test
    void toDtoOutList_mapsList() {
        List<RedirectUriDtoOut> result = mapper.toDtoOutList(List.of(redirectUri()));

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(redirectUriDtoOut(REDIRECT_URI_ID)));
    }
}
