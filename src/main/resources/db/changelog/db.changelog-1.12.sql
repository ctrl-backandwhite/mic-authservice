-- Redirect URI para el dominio personalizado nx036.com
INSERT INTO redirect_uris (name, value, enabled)
VALUES ('nx036 dev callback', 'https://nx036.com/nexa-auth/auth/callback', true)
ON CONFLICT (value) DO NOTHING;

INSERT INTO oauthclient_redirecturis (oauthclient_id, redirecturi_id)
SELECT c.id, r.id FROM oauth_clients c, redirect_uris r
WHERE c.client_id = 'oidc-client'
  AND r.value = 'https://nx036.com/nexa-auth/auth/callback'
  AND NOT EXISTS (
    SELECT 1 FROM oauthclient_redirecturis
    WHERE oauthclient_id = c.id AND redirecturi_id = r.id
  );
