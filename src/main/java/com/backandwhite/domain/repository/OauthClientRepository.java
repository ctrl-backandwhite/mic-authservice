package com.backandwhite.domain.repository;

import com.backandwhite.domain.model.OauthClient;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthClientRepository extends BaseRepository<OauthClient, OauthClient, Long> {
}
