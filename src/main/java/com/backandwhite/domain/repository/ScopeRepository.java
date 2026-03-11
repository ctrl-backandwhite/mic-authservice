package com.backandwhite.domain.repository;

import com.backandwhite.common.domain.repository.BaseRepository;
import com.backandwhite.domain.model.Scope;
import org.springframework.stereotype.Repository;

@Repository
public interface ScopeRepository extends BaseRepository<Scope, Scope, Long> {
}
