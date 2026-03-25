package com.backandwhite.infrastructure.db.postgres.repository;

import com.backandwhite.infrastructure.db.postgres.entity.OauthClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthClientJpaRepositoryAdapter extends JpaRepository<OauthClientEntity, Long> {
    OauthClientEntity findByClientId(String clientId);
}
