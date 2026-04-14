-- Redirect URI del API Gateway para el flujo OAuth2
INSERT INTO redirect_uris (name, value, enabled)
VALUES ('gateway callback', 'http://localhost:9000/nexa-auth/auth/callback', true)
ON CONFLICT (value) DO NOTHING;

INSERT INTO oauthclient_redirecturis (oauthclient_id, redirecturi_id)
SELECT c.id, r.id FROM oauth_clients c, redirect_uris r
WHERE c.client_id = 'oidc-client'
  AND r.value = 'http://localhost:9000/nexa-auth/auth/callback'
  AND NOT EXISTS (
    SELECT 1 FROM oauthclient_redirecturis
    WHERE oauthclient_id = c.id AND redirecturi_id = r.id
  );
