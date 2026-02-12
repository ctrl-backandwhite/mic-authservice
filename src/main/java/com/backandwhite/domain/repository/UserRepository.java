package com.backandwhite.domain.repository;

import com.backandwhite.domain.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User, User, Long> {
}
