package com.backandwhite.application.handler;

import static com.backandwhite.provider.GrantTypeProvider.grantType;
import static com.backandwhite.provider.RedirectUriProvider.redirectUri;
import static com.backandwhite.provider.ScopeProvider.openidScope;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.backandwhite.domain.model.GrantType;
import com.backandwhite.domain.model.OauthClient;
import com.backandwhite.domain.model.RedirectUri;
import com.backandwhite.domain.model.Scope;
import com.backandwhite.domain.repository.GrantTypeRepository;
import com.backandwhite.domain.repository.RedirectUriRepository;
import com.backandwhite.domain.repository.ScopeRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OauthClientCommandHandlerTest {

    @Mock
    private ScopeRepository scopeRepository;

    @Mock
    private RedirectUriRepository redirectUriRepository;

    @Mock
    private GrantTypeRepository grantTypeRepository;

    @InjectMocks
    private OauthClientCommandHandler oauthClientCommandHandler;

    @Test
    void validate_withScopes_fetchesAndSetsScopes() {
        Scope inputScope = Scope.builder().id(3L).build();
        Scope fetched = openidScope();
        OauthClient client = OauthClient.builder().scopes(new ArrayList<>(List.of(inputScope))).redirectUris(null)
                .grantTypes(null).build();

        when(scopeRepository.getById(3L)).thenReturn(fetched);

        oauthClientCommandHandler.validate(client);

        verify(scopeRepository).getById(3L);
        assertThat(client.getScopes()).containsExactly(fetched);
    }

    @Test
    void validate_withRedirectUris_fetchesAndSetsRedirectUris() {
        RedirectUri inputUri = RedirectUri.builder().id(1L).build();
        RedirectUri fetched = redirectUri();
        OauthClient client = OauthClient.builder().scopes(null).redirectUris(new ArrayList<>(List.of(inputUri)))
                .grantTypes(null).build();

        when(redirectUriRepository.getById(1L)).thenReturn(fetched);

        oauthClientCommandHandler.validate(client);

        verify(redirectUriRepository).getById(1L);
        assertThat(client.getRedirectUris()).containsExactly(fetched);
    }

    @Test
    void validate_withGrantTypes_fetchesAndSetsGrantTypes() {
        GrantType inputGt = GrantType.builder().id(1L).build();
        GrantType fetched = grantType();
        OauthClient client = OauthClient.builder().scopes(null).redirectUris(null)
                .grantTypes(new ArrayList<>(List.of(inputGt))).build();

        when(grantTypeRepository.getById(1L)).thenReturn(fetched);

        oauthClientCommandHandler.validate(client);

        verify(grantTypeRepository).getById(1L);
        assertThat(client.getGrantTypes()).containsExactly(fetched);
    }

    @Test
    void validate_withNullCollections_skipsAllValidation() {
        OauthClient client = OauthClient.builder().scopes(null).redirectUris(null).grantTypes(null).build();

        oauthClientCommandHandler.validate(client);

        verifyNoInteractions(scopeRepository);
        verifyNoInteractions(redirectUriRepository);
        verifyNoInteractions(grantTypeRepository);
    }

    @Test
    void validate_withEmptyCollections_skipsAllValidation() {
        OauthClient client = OauthClient.builder().scopes(Collections.emptyList()).redirectUris(Collections.emptyList())
                .grantTypes(Collections.emptyList()).build();

        oauthClientCommandHandler.validate(client);

        verifyNoInteractions(scopeRepository);
        verifyNoInteractions(redirectUriRepository);
        verifyNoInteractions(grantTypeRepository);
    }
}
