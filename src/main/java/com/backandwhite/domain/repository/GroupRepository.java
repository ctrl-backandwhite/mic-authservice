package com.backandwhite.domain.repository;

import com.backandwhite.domain.model.Group;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends BaseRepository<Group, Group, Long> {
}
