package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.Group;
import com.backandwhite.infrastructure.db.postgres.entity.GroupEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Field;
import java.util.List;

import static com.backandwhite.provider.GroupProvider.group;
import static com.backandwhite.provider.GroupProvider.groupEntity;
import static org.assertj.core.api.Assertions.assertThat;

class GroupEntityMapperTest {

    private GroupEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(GroupEntityMapper.class);
        RoleEntityMapper roleEntityMapper = Mappers.getMapper(RoleEntityMapper.class);
        setField(mapper, "roleEntityMapper", roleEntityMapper);
    }

    @Test
    void toDomain_mapsEntityToDomain() {
        GroupEntity entity = groupEntity();

        Group result = mapper.toDomain(entity);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(group());
    }

    @Test
    void toEntity_mapsDomainToEntity() {
        Group model = group();

        GroupEntity result = mapper.toEntity(model);

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .isEqualTo(groupEntity());
    }

    @Test
    void toDomainList_mapsEntitiesToDomainList() {
        List<GroupEntity> entities = List.of(groupEntity());

        List<Group> result = mapper.toDomainList(entities);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(group()));
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new IllegalStateException("Unable to set mapper dependency", exception);
        }
    }
}
