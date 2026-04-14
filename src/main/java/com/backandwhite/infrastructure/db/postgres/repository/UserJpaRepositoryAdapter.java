package com.backandwhite.infrastructure.db.postgres.repository;

import com.backandwhite.infrastructure.db.postgres.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepositoryAdapter extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    UserEntity findByNickName(String nickName);

    UserEntity findByActivationToken(String activationToken);

    UserEntity findByPasswordResetToken(String passwordResetToken);

    UserEntity findByPasswordChangeCode(String passwordChangeCode);

    List<UserEntity> findByRolesId(Long roleId);

    List<UserEntity> findByGroupsId(Long groupId);
}
