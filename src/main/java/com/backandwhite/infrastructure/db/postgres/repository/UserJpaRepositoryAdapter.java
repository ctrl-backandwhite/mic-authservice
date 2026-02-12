package com.backandwhite.infrastructure.db.postgres.repository;

import com.backandwhite.infrastructure.db.postgres.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepositoryAdapter extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);
}
