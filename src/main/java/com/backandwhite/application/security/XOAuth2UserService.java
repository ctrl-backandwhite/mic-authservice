package com.backandwhite.application.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Custom OAuth2UserService that adapts X (Twitter) API v2's user-info response
 * to Spring Security's expected flat attribute shape.
 * <p>
 * X returns {@code {"data": {"id": "...", "name": "...", "username": "..."}}}.
 * Spring's {@link DefaultOAuth2UserService} reads attributes from the root of
 * the JSON and tries to materialise a {@link DefaultOAuth2User} immediately,
 * which fails with {@code "Attribute value for 'id' cannot be null"} because
 * the configured name-attribute lives one level deeper.
 * <p>
 * To work around it we issue the user-info call ourselves for the {@code x}
 * registration, flatten the {@code data} object into the attribute map and
 * build the {@link DefaultOAuth2User} from there. All other providers fall
 * through to the default service unchanged.
 */
@Log4j2
public class XOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String X_REGISTRATION_ID = "x";

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final RestClient restClient = RestClient.create();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!X_REGISTRATION_ID.equalsIgnoreCase(registrationId)) {
            return delegate.loadUser(userRequest);
        }
        return loadXUser(userRequest);
    }

    private OAuth2User loadXUser(OAuth2UserRequest userRequest) {
        String userInfoUri = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
        String token = userRequest.getAccessToken().getTokenValue();

        Map<String, Object> body;
        try {
            ResponseEntity<Map<String, Object>> response = restClient.method(org.springframework.http.HttpMethod.GET)
                    .uri(userInfoUri + "?user.fields=id,name,username")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {
                    });
            body = response.getBody();
        } catch (RestClientException ex) {
            log.error("::> [X-OAUTH2] Failed to fetch user info reason={}", ex.getMessage());
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info_response",
                    "Failed to retrieve user info from X: " + ex.getMessage(), null), ex);
        }

        if (body == null || !(body.get("data") instanceof Map<?, ?> dataMap)) {
            log.warn("::> [X-OAUTH2] Unexpected user-info response shape body={}", body);
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info_response",
                    "Missing 'data' object in X user-info response", null));
        }

        Map<String, Object> attributes = new HashMap<>();
        for (Map.Entry<?, ?> entry : dataMap.entrySet()) {
            if (entry.getKey() instanceof String key) {
                attributes.put(key, entry.getValue());
            }
        }
        Object id = attributes.get("id");
        if (id == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info_response",
                    "X user-info response is missing the 'id' field", null));
        }

        // Mint a synthetic email keyed by the X numeric id (same recipe as
        // GoogleOAuth2SuccessHandler.handleX) and stash it under the "email"
        // attribute. This makes principal.getName() equal to the e-mail that
        // we use as the User row primary lookup, so downstream consumers
        // (UserTokenCustomizer, JWT claims, audit) keep working with a single
        // identifier across the form-login and X-login paths.
        String syntheticEmail = ("x-" + id + "@x.local").toLowerCase();
        attributes.put("email", syntheticEmail);

        log.debug("::> [X-OAUTH2] Loaded user attributes keys={} principalName={}", attributes.keySet(),
                syntheticEmail);
        return new DefaultOAuth2User(Set.of(new SimpleGrantedAuthority("ROLE_USER")), attributes, "email");
    }
}
