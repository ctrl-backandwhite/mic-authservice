package com.backandwhite.domain.repository;

import com.backandwhite.common.domain.repository.BaseRepository;
import com.backandwhite.domain.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends BaseRepository<User, User, Long> {

    User findUserByEmail(String email);

    User findUserByNickName(String nickName);

    User findByActivationToken(String activationToken);

    User findByPasswordResetToken(String token);

    List<User> findByRoleId(Long roleId);

    List<User> findByGroupId(Long groupId);
}
