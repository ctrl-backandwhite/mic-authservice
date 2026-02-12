package com.backandwhite.api.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;


@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OauthClientDtoIn {

    @NotEmpty
    @Schema(
        description = "ID único del cliente OAuth2. Se utiliza para identificar la aplicación cliente",
        example = "mi-app-web",
        minLength = 3,
        maxLength = 100
    )
    private String clientId;

    @NotEmpty
    @Schema(
        description = "Secreto del cliente OAuth2. Debe mantenerse confidencial y nunca exponerse al navegador",
        example = "abc123xyz789secret",
        minLength = 8,
        maxLength = 255
    )
    private String clientSecret;

    @ArraySchema(
        schema = @Schema(
            description = "ID del scope",
            example = "1",
            minimum = "1"
        ),
        arraySchema = @Schema(
            description = "Lista de IDs de scopes que el cliente puede solicitar",
            example = "[1, 2, 3]"
        )
    )
    private List<Long> scopeIds;

    @ArraySchema(
        schema = @Schema(
            description = "ID del URI de redirección",
            example = "1",
            minimum = "1"
        ),
        arraySchema = @Schema(
            description = "Lista de IDs de URIs de redirección autorizadas",
            example = "[1, 2]"
        )
    )
    private List<Long> redirectUriIds;

    @ArraySchema(
        schema = @Schema(
            description = "ID del tipo de concesión",
            example = "1",
            minimum = "1"
        ),
        arraySchema = @Schema(
            description = "Lista de IDs de tipos de concesión OAuth2 permitidos (authorization_code, implicit, etc.)",
            example = "[1, 2]"
        )
    )
    private List<Long> grantTypeIds;
}
