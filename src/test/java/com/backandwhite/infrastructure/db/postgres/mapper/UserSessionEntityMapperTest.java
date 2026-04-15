package com.backandwhite.infrastructure.db.postgres.mapper;

import static com.backandwhite.provider.UserSessionProvider.userSession;
import static com.backandwhite.provider.UserSessionProvider.userSessionEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.domain.model.UserSession;
import com.backandwhite.infrastructure.db.postgres.entity.UserSessionEntity;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserSessionEntityMapperTest {

    private UserSessionEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserSessionEntityMapper.class);
    }

    @Test
    void toDomain_mapsEntityToDomain() {
        UserSessionEntity entity = userSessionEntity();

        UserSession result = mapper.toDomain(entity);

        assertThat(result).usingRecursiveComparison().isEqualTo(userSession());
    }

    @Test
    void toEntity_mapsDomainToEntity() {
        UserSession model = userSession();

        UserSessionEntity result = mapper.toEntity(model);

        assertThat(result).usingRecursiveComparison().isEqualTo(userSessionEntity());
    }

    @Test
    void toDomainList_mapsEntitiesToDomainList() {
        List<UserSessionEntity> entities = List.of(userSessionEntity());

        List<UserSession> result = mapper.toDomainList(entities);

        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(userSession()));
    }
}
