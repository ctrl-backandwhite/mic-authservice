package com.backandwhite.domain.model;

import lombok.*;


import java.util.List;
import java.util.ArrayList;


@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OauthClient {

    private Long id;
    private String clientId;
    private String clientSecret;
    @Builder.Default
    private List<Scope> scopes = new ArrayList<>();
    @Builder.Default
    private List<RedirectUri> redirectUris = new ArrayList<>();
    @Builder.Default
    private List<GrantType> grantTypes = new ArrayList<>();

    public void addScope(List<Scope> scopes) {
        this.scopes.addAll(scopes);
    }

    public void removeScope(List<Scope> scopes) {
        this.scopes.removeAll(scopes);
    }

    public void addRedirectUri(List<RedirectUri> redirectUris) {
        this.redirectUris.addAll(redirectUris);
    }

    public void removeRedirectUri(List<RedirectUri> redirectUris) {
        this.redirectUris.removeAll(redirectUris);
    }

    public void addGrantType(List<GrantType> grantTypes) {
        this.grantTypes.addAll(grantTypes);
    }

    public void removeGrantType(List<GrantType> grantTypes) {
        this.grantTypes.removeAll(grantTypes);
    }

}
