package com.backandwhite.api.mapper;

import static com.backandwhite.provider.GrantTypeProvider.GRANT_TYPE_ID;
import static com.backandwhite.provider.OauthClientProvider.CLIENT_CLIENT_ID;
import static com.backandwhite.provider.OauthClientProvider.CLIENT_ID;
import static com.backandwhite.provider.OauthClientProvider.CLIENT_SECRET;
import static com.backandwhite.provider.OauthClientProvider.oauthClient;
import static com.backandwhite.provider.OauthClientProvider.oauthClientDtoIn;
import static com.backandwhite.provider.OauthClientProvider.oauthClientDtoOut;
import static com.backandwhite.provider.RedirectUriProvider.REDIRECT_URI_ID;
import static com.backandwhite.provider.ScopeProvider.OPENID_ID;
import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.api.dto.in.OauthClientDtoIn;
import com.backandwhite.api.dto.out.OauthClientDtoOut;
import com.backandwhite.domain.model.GrantType;
import com.backandwhite.domain.model.OauthClient;
import com.backandwhite.domain.model.RedirectUri;
import com.backandwhite.domain.model.Scope;
import com.backandwhite.util.MapperTestUtils;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class OauthClientDtoMapperTest {

    private OauthClientDtoMapper mapper;

    @BeforeEach
    void setUp() {
        ScopeDtoMapper scopeDtoMapper = Mappers.getMapper(ScopeDtoMapper.class);
        RedirectUriDtoMapper redirectUriDtoMapper = Mappers.getMapper(RedirectUriDtoMapper.class);
        GrantTypeDtoMapper grantTypeDtoMapper = Mappers.getMapper(GrantTypeDtoMapper.class);

        mapper = Mappers.getMapper(OauthClientDtoMapper.class);
        MapperTestUtils.setField(mapper, "scopeDtoMapper", scopeDtoMapper);
        MapperTestUtils.setField(mapper, "redirectUriDtoMapper", redirectUriDtoMapper);
        MapperTestUtils.setField(mapper, "grantTypeDtoMapper", grantTypeDtoMapper);
    }

    @Test
    void toDtoOut_mapsDomainToDtoOut() {
        OauthClient model = oauthClient();

        OauthClientDtoOut result = mapper.toDtoOut(model);

        assertThat(result).usingRecursiveComparison().isEqualTo(oauthClientDtoOut(CLIENT_ID));
    }

    @Test
    void toDomain_mapsDtoInToDomain() {
        OauthClientDtoIn dtoIn = oauthClientDtoIn();

        OauthClient result = mapper.toDomain(dtoIn);

        OauthClient expected = OauthClient.builder().clientId(CLIENT_CLIENT_ID).clientSecret(CLIENT_SECRET)
                .scopes(List.of(Scope.builder().id(OPENID_ID).build()))
                .redirectUris(List.of(RedirectUri.builder().id(REDIRECT_URI_ID).build()))
                .grantTypes(List.of(GrantType.builder().id(GRANT_TYPE_ID).build())).build();

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toDtoOutList_mapsList() {
        List<OauthClientDtoOut> result = mapper.toDtoOutList(List.of(oauthClient()));

        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(oauthClientDtoOut(CLIENT_ID)));
    }

    @Test
    void mapScopeIds_nullReturnsEmptyList() {
        assertThat(mapper.mapScopeIds(null)).isEmpty();
    }

    @Test
    void mapScopeIds_emptyReturnsEmptyList() {
        assertThat(mapper.mapScopeIds(Collections.emptyList())).isEmpty();
    }

    @Test
    void mapScopeIds_withIdsReturnScopes() {
        List<Scope> result = mapper.mapScopeIds(List.of(1L, 2L));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void mapRedirectUriIds_nullReturnsEmptyList() {
        assertThat(mapper.mapRedirectUriIds(null)).isEmpty();
    }

    @Test
    void mapRedirectUriIds_emptyReturnsEmptyList() {
        assertThat(mapper.mapRedirectUriIds(Collections.emptyList())).isEmpty();
    }

    @Test
    void mapRedirectUriIds_withIdsReturnRedirectUris() {
        List<RedirectUri> result = mapper.mapRedirectUriIds(List.of(1L, 2L));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void mapGrantTypeIds_nullReturnsEmptyList() {
        assertThat(mapper.mapGrantTypeIds(null)).isEmpty();
    }

    @Test
    void mapGrantTypeIds_emptyReturnsEmptyList() {
        assertThat(mapper.mapGrantTypeIds(Collections.emptyList())).isEmpty();
    }

    @Test
    void mapGrantTypeIds_withIdsReturnGrantTypes() {
        List<GrantType> result = mapper.mapGrantTypeIds(List.of(1L, 2L));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }
}
