package com.backandwhite.api.mapper;

import com.backandwhite.api.dto.out.UserSessionDtoOut;
import com.backandwhite.domain.model.UserSession;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSessionDtoMapper {

    UserSessionDtoOut toDtoOut(UserSession session);

    List<UserSessionDtoOut> toDtoOutList(List<UserSession> sessions);
}
