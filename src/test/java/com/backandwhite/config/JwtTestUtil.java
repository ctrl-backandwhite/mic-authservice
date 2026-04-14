package com.backandwhite.config;

import com.backandwhite.domain.model.User;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

@Profile("test")
@RequiredArgsConstructor
public class JwtTestUtil {

    private final JwtEncoder jwtEncoder;

    public Jwt createJwt(String subject, List<String> roles, Map<String, Object> additionalClaims) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(3600);

        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "access token");
        claims.put("roles", roles);
        if (additionalClaims != null) {
            claims.putAll(additionalClaims);
        }

        JwtClaimsSet claimsSet = JwtClaimsSet.builder().id(UUID.randomUUID().toString()).issuer("http://localhost:8443")
                .subject(subject).issuedAt(now).expiresAt(expiry).claims(map -> map.putAll(claims)).build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claimsSet));
    }

    public Jwt createJwt(User user) {
        Map<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("user_id", user.getId());
        additionalClaims.put("name", user.getName());
        additionalClaims.put("last_name", user.getLastName());
        additionalClaims.put("enabled", user.getEnabled());

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getUniqueName() != null ? role.getUniqueName() : role.getName()).toList();

        return createJwt(user.getEmail(), roles, additionalClaims);
    }

    public String getToken(String subject, List<String> roles) {
        return "Bearer " + createJwt(subject, roles, null).getTokenValue();
    }

    public String getToken(String subject, List<String> roles, Map<String, Object> additionalClaims) {
        return "Bearer " + createJwt(subject, roles, additionalClaims).getTokenValue();
    }

    public String getToken(User user) {
        return "Bearer " + createJwt(user).getTokenValue();
    }
}
