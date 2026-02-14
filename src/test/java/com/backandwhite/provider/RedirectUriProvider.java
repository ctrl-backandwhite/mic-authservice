package com.backandwhite.provider;

import com.backandwhite.domain.model.RedirectUri;
import com.backandwhite.infrastructure.db.postgres.entity.RedirectUriEntity;

public final class RedirectUriProvider {

    public static final Long REDIRECT_URI_ID = 1L;
    public static final String REDIRECT_URI_NAME = "Web";
    public static final String REDIRECT_URI_VALUE = "https://example.com/callback";
    public static final Boolean REDIRECT_URI_ENABLED = true;

    public static final Long OTHER_REDIRECT_URI_ID = 2L;
    public static final String OTHER_REDIRECT_URI_NAME = "Mobile";
    public static final String OTHER_REDIRECT_URI_VALUE = "app://callback";
    public static final Boolean OTHER_REDIRECT_URI_ENABLED = false;

    private RedirectUriProvider() {
        // Utility class.
    }

    public static RedirectUri redirectUri() {
        return RedirectUri.builder()
                .id(REDIRECT_URI_ID)
                .name(REDIRECT_URI_NAME)
                .value(REDIRECT_URI_VALUE)
                .enabled(REDIRECT_URI_ENABLED)
                .build();
    }

    public static RedirectUri otherRedirectUri() {
        return RedirectUri.builder()
                .id(OTHER_REDIRECT_URI_ID)
                .name(OTHER_REDIRECT_URI_NAME)
                .value(OTHER_REDIRECT_URI_VALUE)
                .enabled(OTHER_REDIRECT_URI_ENABLED)
                .build();
    }

    public static RedirectUriEntity redirectUriEntity() {
        return RedirectUriEntity.builder()
                .id(REDIRECT_URI_ID)
                .name(REDIRECT_URI_NAME)
                .value(REDIRECT_URI_VALUE)
                .enabled(REDIRECT_URI_ENABLED)
                .build();
    }
}
