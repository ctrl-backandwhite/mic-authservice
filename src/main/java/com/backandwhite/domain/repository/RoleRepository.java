package com.backandwhite.domain.repository;

import com.backandwhite.domain.model.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseRepository<Role, Role, Long> {
}
