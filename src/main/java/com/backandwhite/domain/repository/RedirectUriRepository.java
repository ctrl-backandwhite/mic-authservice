package com.backandwhite.domain.repository;

import com.backandwhite.common.domain.repository.BaseRepository;
import com.backandwhite.domain.model.RedirectUri;
import org.springframework.stereotype.Repository;

@Repository
public interface RedirectUriRepository extends BaseRepository<RedirectUri, RedirectUri, Long> {
}
