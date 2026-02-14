package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.OauthClient;
import com.backandwhite.infrastructure.db.postgres.entity.OauthClientEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Field;
import java.util.List;

import static com.backandwhite.provider.OauthClientProvider.oauthClient;
import static com.backandwhite.provider.OauthClientProvider.oauthClientEntity;
import static org.assertj.core.api.Assertions.assertThat;

class OauthClientEntityMapperTest {

    private OauthClientEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(OauthClientEntityMapper.class);
        ScopeEntityMapper scopeEntityMapper = Mappers.getMapper(ScopeEntityMapper.class);
        RedirectUriEntityMapper redirectUriEntityMapper = Mappers.getMapper(RedirectUriEntityMapper.class);
        GrantTypeEntityMapper grantTypeEntityMapper = Mappers.getMapper(GrantTypeEntityMapper.class);

        setMapperDependency(mapper, ScopeEntityMapper.class, scopeEntityMapper);
        setMapperDependency(mapper, RedirectUriEntityMapper.class, redirectUriEntityMapper);
        setMapperDependency(mapper, GrantTypeEntityMapper.class, grantTypeEntityMapper);
    }

    @Test
    void toDomain_mapsEntityToDomain() {
        OauthClientEntity entity = oauthClientEntity();

        OauthClient result = mapper.toDomain(entity);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(oauthClient());
    }

    @Test
    void toEntity_mapsDomainToEntity() {
        OauthClient model = oauthClient();

        OauthClientEntity result = mapper.toEntity(model);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(oauthClientEntity());
    }

    @Test
    void toDomainList_mapsEntitiesToDomainList() {
        List<OauthClientEntity> entities = List.of(oauthClientEntity());

        List<OauthClient> result = mapper.toDomainList(entities);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(oauthClient()));
    }

    private static <T> void setMapperDependency(Object target, Class<T> type, T value) {
        for (Field field : target.getClass().getDeclaredFields()) {
            if (type.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    field.set(target, value);
                    return;
                } catch (IllegalAccessException exception) {
                    throw new IllegalStateException("Unable to set mapper dependency", exception);
                }
            }
        }
        throw new IllegalStateException("Mapper dependency not found: " + type.getName());
    }
}
