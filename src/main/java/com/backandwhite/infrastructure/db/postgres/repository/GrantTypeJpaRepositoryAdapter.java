package com.backandwhite.infrastructure.db.postgres.repository;

import com.backandwhite.infrastructure.db.postgres.entity.GrantTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrantTypeJpaRepositoryAdapter extends JpaRepository<GrantTypeEntity, Long> {
}
