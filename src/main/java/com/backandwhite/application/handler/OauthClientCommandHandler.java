package com.backandwhite.application.handler;

import com.backandwhite.domain.model.OauthClient;
import com.backandwhite.domain.model.Scope;
import com.backandwhite.domain.repository.ScopeRepository;
import com.backandwhite.domain.model.RedirectUri;
import com.backandwhite.domain.repository.RedirectUriRepository;
import com.backandwhite.domain.model.GrantType;
import com.backandwhite.domain.repository.GrantTypeRepository;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@Component
@AllArgsConstructor
public class OauthClientCommandHandler {

    private final ScopeRepository scopeRepository;
    private final RedirectUriRepository redirectUriRepository;
    private final GrantTypeRepository grantTypeRepository;

    public void validate(OauthClient oauthClient) {
        validateScope(oauthClient);
        validateRedirectUri(oauthClient);
        validateGrantType(oauthClient);
    }


    private void validateScope(OauthClient oauthClient) {

        if (Objects.isNull(oauthClient.getScopes()) || oauthClient.getScopes().isEmpty()) {
            log.info("::> No scope to associate to oauthclient {}", oauthClient.getScopes());
            return;
        }

        List<Scope> scopesList = new ArrayList<>();
        oauthClient.getScopes().forEach(scope -> {
            Scope response = scopeRepository.getById(scope.getId());
            scopesList.add(response);
        });
        oauthClient.setScopes(scopesList);
    }


    private void validateRedirectUri(OauthClient oauthClient) {

        if (Objects.isNull(oauthClient.getRedirectUris()) || oauthClient.getRedirectUris().isEmpty()) {
            log.info("::> No redirectUri to associate to oauthclient {}", oauthClient.getRedirectUris());
            return;
        }

        List<RedirectUri> redirectUrisList = new ArrayList<>();
        oauthClient.getRedirectUris().forEach(redirectUri -> {
            RedirectUri response = redirectUriRepository.getById(redirectUri.getId());
            redirectUrisList.add(response);
        });
        oauthClient.setRedirectUris(redirectUrisList);
    }


    private void validateGrantType(OauthClient oauthClient) {

        if (Objects.isNull(oauthClient.getGrantTypes()) || oauthClient.getGrantTypes().isEmpty()) {
            log.info("::> No grantType to associate to oauthclient {}", oauthClient.getGrantTypes());
            return;
        }

        List<GrantType> grantTypesList = new ArrayList<>();
        oauthClient.getGrantTypes().forEach(grantType -> {
            GrantType response = grantTypeRepository.getById(grantType.getId());
            grantTypesList.add(response);
        });
        oauthClient.setGrantTypes(grantTypesList);
    }

}
