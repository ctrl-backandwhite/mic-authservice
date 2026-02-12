package com.backandwhite.api.dto.out;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


import java.util.List;
import java.util.ArrayList;


@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OauthClientDtoOut {

    @Schema(
        description = "Identificador único del cliente OAuth2",
        example = "1",
        minimum = "1"
    )
    private Long id;

    @Schema(
        description = "ID único del cliente OAuth2. Se utiliza para identificar la aplicación cliente",
        example = "mi-app-web",
        minLength = 3,
        maxLength = 100
    )
    private String clientId;

    @Schema(
        description = "Secreto del cliente OAuth2. Debe mantenerse confidencial",
        example = "abc123xyz789secret",
        minLength = 8,
        maxLength = 255
    )
    private String clientSecret;

    @ArraySchema(
        schema = @Schema(implementation = ScopeDtoOut.class),
        arraySchema = @Schema(
            description = "Scopes que el cliente puede solicitar"
        )
    )
    private List<ScopeDtoOut> scopes = new ArrayList<>();

    @ArraySchema(
        schema = @Schema(implementation = RedirectUriDtoOut.class),
        arraySchema = @Schema(
            description = "URIs de redirección autorizadas"
        )
    )
    private List<RedirectUriDtoOut> redirectUris = new ArrayList<>();

    @ArraySchema(
        schema = @Schema(implementation = GrantTypeDtoOut.class),
        arraySchema = @Schema(
            description = "Tipos de concesión OAuth2 permitidos"
        )
    )
    private List<GrantTypeDtoOut> grantTypes = new ArrayList<>();
}
