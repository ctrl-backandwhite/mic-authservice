package com.backandwhite.infrastructure.db.postgres.mapper;

import com.backandwhite.domain.model.User;
import com.backandwhite.infrastructure.db.postgres.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        RoleEntityMapper.class,
        GroupEntityMapper.class
})
public interface UserEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "nickName", source = "nickName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "accountNonExpired", source = "accountNonExpired")
    @Mapping(target = "accountNonLocked", source = "accountNonLocked")
    @Mapping(target = "credentialsNonExpired", source = "credentialsNonExpired")
    @Mapping(target = "activationToken", source = "activationToken")
    @Mapping(target = "activationTokenExpiry", source = "activationTokenExpiry")
    @Mapping(target = "passwordResetToken", source = "passwordResetToken")
    @Mapping(target = "passwordResetTokenExpiry", source = "passwordResetTokenExpiry")
    @Mapping(target = "passwordChangeCode", source = "passwordChangeCode")
    @Mapping(target = "passwordChangeCodeExpiry", source = "passwordChangeCodeExpiry")
    @Mapping(target = "sessionRevokeCode", source = "sessionRevokeCode")
    @Mapping(target = "sessionRevokeCodeExpiry", source = "sessionRevokeCodeExpiry")
    @Mapping(target = "sessionToRevoke", source = "sessionToRevoke")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "groups", source = "groups")
    User toDomain(UserEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "nickName", source = "nickName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "accountNonExpired", source = "accountNonExpired")
    @Mapping(target = "accountNonLocked", source = "accountNonLocked")
    @Mapping(target = "credentialsNonExpired", source = "credentialsNonExpired")
    @Mapping(target = "activationToken", source = "activationToken")
    @Mapping(target = "activationTokenExpiry", source = "activationTokenExpiry")
    @Mapping(target = "passwordResetToken", source = "passwordResetToken")
    @Mapping(target = "passwordResetTokenExpiry", source = "passwordResetTokenExpiry")
    @Mapping(target = "passwordChangeCode", source = "passwordChangeCode")
    @Mapping(target = "passwordChangeCodeExpiry", source = "passwordChangeCodeExpiry")
    @Mapping(target = "sessionRevokeCode", source = "sessionRevokeCode")
    @Mapping(target = "sessionRevokeCodeExpiry", source = "sessionRevokeCodeExpiry")
    @Mapping(target = "sessionToRevoke", source = "sessionToRevoke")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "groups", source = "groups")
    UserEntity toEntity(User model);

    List<User> toDomainList(List<UserEntity> entities);

    List<UserEntity> toEntityList(List<User> models);
}
