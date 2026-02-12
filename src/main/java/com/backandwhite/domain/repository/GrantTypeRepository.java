package com.backandwhite.domain.repository;

import com.backandwhite.domain.model.GrantType;
import org.springframework.stereotype.Repository;

@Repository
public interface GrantTypeRepository extends BaseRepository<GrantType, GrantType, Long> {
}
