package com.backandwhite.domain.repository;

import com.backandwhite.common.domain.repository.BaseRepository;
import com.backandwhite.domain.model.OauthClient;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthClientRepository extends BaseRepository<OauthClient, OauthClient, Long> {

    /**
     * Busca un cliente OAuth por su clientId
     * 
     * @param clientId el identificador del cliente
     * @return el cliente OAuth o null si no existe
     */
    OauthClient findByClientId(String clientId);
}
