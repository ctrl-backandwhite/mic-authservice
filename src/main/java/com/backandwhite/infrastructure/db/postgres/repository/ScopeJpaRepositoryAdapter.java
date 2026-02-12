package com.backandwhite.infrastructure.db.postgres.repository;

import com.backandwhite.infrastructure.db.postgres.entity.ScopeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScopeJpaRepositoryAdapter extends JpaRepository<ScopeEntity, Long> {
}
