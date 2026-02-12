package com.backandwhite.infrastructure.db.postgres.repository;

import com.backandwhite.infrastructure.db.postgres.entity.RedirectUriEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RedirectUriJpaRepositoryAdapter extends JpaRepository<RedirectUriEntity, Long> {
}
