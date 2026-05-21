package com.backandwhite.application.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;

class PublicClientRefreshTokenAuthenticationConverterTest {

    private PublicClientRefreshTokenAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        converter = new PublicClientRefreshTokenAuthenticationConverter();
    }

    @Test
    void convert_refreshTokenWithClientId_returnsAuthentication() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter(OAuth2ParameterNames.GRANT_TYPE)).thenReturn("refresh_token");
        when(request.getParameter(OAuth2ParameterNames.CLIENT_ID)).thenReturn("my-client");
        when(request.getParameter(OAuth2ParameterNames.CLIENT_SECRET)).thenReturn(null);

        Authentication result = converter.convert(request);

        assertThat(result).isNotNull().isInstanceOf(OAuth2ClientAuthenticationToken.class);
    }

    @Test
    void convert_nonRefreshTokenGrant_returnsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter(OAuth2ParameterNames.GRANT_TYPE)).thenReturn("authorization_code");

        Authentication result = converter.convert(request);

        assertThat(result).isNull();
    }

    @Test
    void convert_noClientId_returnsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter(OAuth2ParameterNames.GRANT_TYPE)).thenReturn("refresh_token");
        when(request.getParameter(OAuth2ParameterNames.CLIENT_ID)).thenReturn(null);

        Authentication result = converter.convert(request);

        assertThat(result).isNull();
    }

    @Test
    void convert_withClientSecret_returnsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter(OAuth2ParameterNames.GRANT_TYPE)).thenReturn("refresh_token");
        when(request.getParameter(OAuth2ParameterNames.CLIENT_ID)).thenReturn("my-client");
        when(request.getParameter(OAuth2ParameterNames.CLIENT_SECRET)).thenReturn("secret");

        Authentication result = converter.convert(request);

        assertThat(result).isNull();
    }
}
