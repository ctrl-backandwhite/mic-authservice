package com.backandwhite.provider;

import com.backandwhite.api.dto.in.RedirectUriDtoIn;
import com.backandwhite.api.dto.out.RedirectUriDtoOut;
import com.backandwhite.domain.model.RedirectUri;
import com.backandwhite.infrastructure.db.postgres.entity.RedirectUriEntity;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public final class RedirectUriProvider {

    public static final Long REDIRECT_URI_ID = 1L;
    public static final String REDIRECT_URI_NAME = "Web";
    public static final String REDIRECT_URI_VALUE = "https://example.com/callback";
    public static final Boolean REDIRECT_URI_ENABLED = true;

    public static final Long OTHER_REDIRECT_URI_ID = 2L;
    public static final String OTHER_REDIRECT_URI_NAME = "Mobile";
    public static final String OTHER_REDIRECT_URI_VALUE = "app://callback";
    public static final Boolean OTHER_REDIRECT_URI_ENABLED = false;

    public static RedirectUri redirectUri() {
        return RedirectUri.builder().id(REDIRECT_URI_ID).name(REDIRECT_URI_NAME).value(REDIRECT_URI_VALUE)
                .enabled(REDIRECT_URI_ENABLED).createdAt(AuditProvider.CREATED_AT).updatedAt(AuditProvider.UPDATED_AT)
                .createdBy(AuditProvider.CREATED_BY).updatedBy(AuditProvider.UPDATED_BY).build();
    }

    public static RedirectUri otherRedirectUri() {
        return RedirectUri.builder().id(OTHER_REDIRECT_URI_ID).name(OTHER_REDIRECT_URI_NAME)
                .value(OTHER_REDIRECT_URI_VALUE).enabled(OTHER_REDIRECT_URI_ENABLED).createdAt(AuditProvider.CREATED_AT)
                .updatedAt(AuditProvider.UPDATED_AT).createdBy(AuditProvider.CREATED_BY)
                .updatedBy(AuditProvider.UPDATED_BY).build();
    }

    public static RedirectUriEntity redirectUriEntity() {
        return RedirectUriEntity.builder().id(REDIRECT_URI_ID).name(REDIRECT_URI_NAME).value(REDIRECT_URI_VALUE)
                .enabled(REDIRECT_URI_ENABLED).createdAt(AuditProvider.CREATED_AT).updatedAt(AuditProvider.UPDATED_AT)
                .createdBy(AuditProvider.CREATED_BY).updatedBy(AuditProvider.UPDATED_BY).build();
    }

    public static RedirectUriEntity otherRedirectUriEntity() {
        return RedirectUriEntity.builder().id(OTHER_REDIRECT_URI_ID).name(OTHER_REDIRECT_URI_NAME)
                .value(OTHER_REDIRECT_URI_VALUE).enabled(OTHER_REDIRECT_URI_ENABLED).createdAt(AuditProvider.CREATED_AT)
                .updatedAt(AuditProvider.UPDATED_AT).createdBy(AuditProvider.CREATED_BY)
                .updatedBy(AuditProvider.UPDATED_BY).build();
    }

    public static RedirectUriDtoIn redirectUriDtoIn() {
        return RedirectUriDtoIn.builder().name(REDIRECT_URI_NAME).value(REDIRECT_URI_VALUE)
                .enabled(REDIRECT_URI_ENABLED).build();
    }

    public static RedirectUriDtoIn otherRedirectUriDtoIn() {
        return RedirectUriDtoIn.builder().name(OTHER_REDIRECT_URI_NAME).value(OTHER_REDIRECT_URI_VALUE)
                .enabled(OTHER_REDIRECT_URI_ENABLED).build();
    }

    public static RedirectUriDtoOut redirectUriDtoOut(Long id) {
        return RedirectUriDtoOut.builder().id(id).name(REDIRECT_URI_NAME).value(REDIRECT_URI_VALUE)
                .enabled(REDIRECT_URI_ENABLED).createdAt(AuditProvider.CREATED_AT).updatedAt(AuditProvider.UPDATED_AT)
                .createdBy(AuditProvider.CREATED_BY).updatedBy(AuditProvider.UPDATED_BY).build();
    }

    public static RedirectUriDtoOut otherRedirectUriDtoOut(Long id) {
        return RedirectUriDtoOut.builder().id(id).name(OTHER_REDIRECT_URI_NAME).value(OTHER_REDIRECT_URI_VALUE)
                .enabled(OTHER_REDIRECT_URI_ENABLED).createdAt(AuditProvider.CREATED_AT)
                .updatedAt(AuditProvider.UPDATED_AT).createdBy(AuditProvider.CREATED_BY)
                .updatedBy(AuditProvider.UPDATED_BY).build();
    }
}
