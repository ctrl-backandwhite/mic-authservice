package com.backandwhite.api.controller;

import com.backandwhite.common.security.annotation.NxUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * NX036SHOP-448 — Endpoints TOTP (scaffolding).
 *
 * <p>El controlador expone el contorno público del flujo TOTP. Las
 * operaciones aún no persisten en {@code user_totp} (tabla creada en
 * {@code db.changelog-1.22.sql}); cada handler devuelve una respuesta
 * placeholder marcada {@code "status":"NOT_IMPLEMENTED"} para que el front
 * pueda integrar sin esperar al servicio backend completo.
 *
 * <p>Flujo esperado (cuando esté implementado):
 * <ol>
 *   <li>POST {@code /enroll} → genera secreto, devuelve QR otpauth URI y
 *       guarda la fila en estado PENDING_VERIFICATION.</li>
 *   <li>POST {@code /verify} con código de 6 dígitos → si valida, status
 *       pasa a ENABLED y se devuelven 10 recovery codes one-shot.</li>
 *   <li>POST {@code /disable} con contraseña + código → marca DISABLED.</li>
 *   <li>POST {@code /validate-login} (interno) → durante el grant
 *       authorization_code, antes de emitir el access_token.</li>
 * </ol>
 */
@Log4j2
@NxUser
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/totp")
@Tag(name = "2FA TOTP", description = "Time-based One-Time Password setup and verification (scaffolding)")
public class TotpController {

    private static final String STATUS_KEY = "status";
    private static final String NOT_IMPLEMENTED = "NOT_IMPLEMENTED";

    @PostMapping("/enroll")
    @Operation(summary = "Generate TOTP secret + QR (scaffolding)",
            description = "Devuelve un secreto base32 y un otpauth URI. NO persiste todavía.")
    public ResponseEntity<Map<String, Object>> enroll(Authentication auth) {
        String secret = randomBase32Secret();
        String userId = auth != null ? auth.getName() : "unknown";
        String otpauthUri = String.format(
                "otpauth://totp/NX036:%s?secret=%s&issuer=NX036&algorithm=SHA1&digits=6&period=30",
                userId, secret);
        log.info("::> [TOTP] enroll requested userId={} (scaffolding)", userId);

        Map<String, Object> body = new HashMap<>();
        body.put("secret", secret);
        body.put("otpauthUri", otpauthUri);
        body.put(STATUS_KEY, NOT_IMPLEMENTED);
        body.put("nextStep", "POST /api/v1/auth/totp/verify with code");
        return ResponseEntity.ok(body);
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify TOTP code (scaffolding)",
            description = "Valida el código de 6 dígitos y emite recovery codes. NO valida todavía.")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody Map<String, String> body) {
        String code = body.getOrDefault("code", "");
        log.info("::> [TOTP] verify code length={} (scaffolding)", code.length());

        Map<String, Object> resp = new HashMap<>();
        resp.put(STATUS_KEY, NOT_IMPLEMENTED);
        resp.put("verified", false);
        resp.put("recoveryCodes", List.<String>of());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/disable")
    @Operation(summary = "Disable TOTP for current user (scaffolding)")
    public ResponseEntity<Map<String, Object>> disable(@RequestBody Map<String, String> body, Authentication auth) {
        String userId = auth != null ? auth.getName() : "unknown";
        log.info("::> [TOTP] disable requested userId={} (scaffolding)", userId);
        return ResponseEntity.ok(Map.of(STATUS_KEY, NOT_IMPLEMENTED, "disabled", false));
    }

    @GetMapping("/status")
    @Operation(summary = "Current TOTP status for user (scaffolding)")
    public ResponseEntity<Map<String, Object>> status(Authentication auth) {
        String userId = auth != null ? auth.getName() : "unknown";
        return ResponseEntity.ok(Map.of(STATUS_KEY, "DISABLED", "userId", userId));
    }

    private String randomBase32Secret() {
        // 20 bytes ≈ 160-bit entropy → 32 caracteres base32 (estándar RFC 6238).
        // Esto es scaffolding: la implementación real persiste y usa un PRNG
        // criptográficamente fuerte en el seedProvider.
        SecureRandom rng = new SecureRandom();
        byte[] bytes = new byte[20];
        rng.nextBytes(bytes);
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < 32; i++) {
            sb.append(alphabet.charAt((bytes[i % bytes.length] & 0xFF) % alphabet.length()));
        }
        return sb.toString();
    }
}
