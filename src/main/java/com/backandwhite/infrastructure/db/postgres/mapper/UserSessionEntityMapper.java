package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.UserSession;
import com.backandwhite.infrastructure.db.postgres.entity.UserSessionEntity;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSessionEntityMapper {

    UserSession toDomain(UserSessionEntity entity);

    UserSessionEntity toEntity(UserSession model);

    List<UserSession> toDomainList(List<UserSessionEntity> entities);
}
