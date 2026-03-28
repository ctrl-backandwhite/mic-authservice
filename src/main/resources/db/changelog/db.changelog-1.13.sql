-- ──────────────────────────────────────────────────────────────────────────────
-- Register ecommerce-client OAuth2 client (public client with PKCE, same as oidc-client)
-- ──────────────────────────────────────────────────────────────────────────────

--changeset authservice:1.13_insert_ecommerce_redirect_uris
INSERT INTO redirect_uris (name, value, enabled) VALUES
('ecommerce-localhost', 'http://localhost:5174/auth/callback', true),
('ecommerce-production', 'https://nx036.com/auth/callback', true)
ON CONFLICT (value) DO NOTHING;

--changeset authservice:1.13_insert_ecommerce_oauth_client
INSERT INTO oauth_clients (client_id, client_secret)
VALUES ('ecommerce-client', '$2a$10$7JGmcJOzzYT3WqKpXy1z1OPST9/PgBkqquzi.Ss7KIUgO2t0jKMm2')
ON CONFLICT (client_id) DO NOTHING;

--changeset authservice:1.13_insert_ecommerce_client_grant_types
INSERT INTO oauthclient_granttypes (oauthclient_id, granttype_id)
SELECT c.id, g.id FROM oauth_clients c, grant_types g
WHERE c.client_id = 'ecommerce-client'
  AND g.name IN ('authorization_code', 'refresh_token')
  AND NOT EXISTS (
    SELECT 1 FROM oauthclient_granttypes
    WHERE oauthclient_id = c.id AND granttype_id = g.id
  );

--changeset authservice:1.13_insert_ecommerce_client_scopes
INSERT INTO oauthclient_scopes (oauthclient_id, scope_id)
SELECT c.id, s.id FROM oauth_clients c, scopes s
WHERE c.client_id = 'ecommerce-client'
  AND s.unique_name IN ('openid', 'profile')
  AND NOT EXISTS (
    SELECT 1 FROM oauthclient_scopes
    WHERE oauthclient_id = c.id AND scope_id = s.id
  );

--changeset authservice:1.13_insert_ecommerce_client_redirect_uris
INSERT INTO oauthclient_redirecturis (oauthclient_id, redirecturi_id)
SELECT c.id, r.id FROM oauth_clients c, redirect_uris r
WHERE c.client_id = 'ecommerce-client'
  AND r.value IN ('http://localhost:5174/auth/callback', 'https://nx036.com/auth/callback')
  AND NOT EXISTS (
    SELECT 1 FROM oauthclient_redirecturis
    WHERE oauthclient_id = c.id AND redirecturi_id = r.id
  );
