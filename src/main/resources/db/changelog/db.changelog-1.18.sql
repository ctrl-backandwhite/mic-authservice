-- ──────────────────────────────────────────────────────────────────────────────
-- Register ecommerce-mobile OAuth2 client (dedicated mobile client, PKCE only)
-- Redirect URI: nx036://auth/callback
-- ──────────────────────────────────────────────────────────────────────────────

--changeset authservice:1.18_insert_ecommerce_mobile_client
INSERT INTO oauth_clients (client_id, client_secret)
VALUES ('ecommerce-mobile', 'placeholder-not-used')
ON CONFLICT (client_id) DO NOTHING;

--changeset authservice:1.18_insert_ecommerce_mobile_grant_types
INSERT INTO oauthclient_granttypes (oauthclient_id, granttype_id)
SELECT c.id, g.id FROM oauth_clients c, grant_types g
WHERE c.client_id = 'ecommerce-mobile'
  AND g.name IN ('authorization_code', 'refresh_token')
  AND NOT EXISTS (
    SELECT 1 FROM oauthclient_granttypes
    WHERE oauthclient_id = c.id AND granttype_id = g.id
  );

--changeset authservice:1.18_insert_ecommerce_mobile_scopes
INSERT INTO oauthclient_scopes (oauthclient_id, scope_id)
SELECT c.id, s.id FROM oauth_clients c, scopes s
WHERE c.client_id = 'ecommerce-mobile'
  AND s.unique_name = 'openid'
  AND NOT EXISTS (
    SELECT 1 FROM oauthclient_scopes
    WHERE oauthclient_id = c.id AND scope_id = s.id
  );

--changeset authservice:1.18_link_ecommerce_mobile_redirect_uri
INSERT INTO oauthclient_redirecturis (oauthclient_id, redirecturi_id)
SELECT c.id, r.id FROM oauth_clients c, redirect_uris r
WHERE c.client_id = 'ecommerce-mobile'
  AND r.value = 'nx036://auth/callback'
  AND NOT EXISTS (
    SELECT 1 FROM oauthclient_redirecturis
    WHERE oauthclient_id = c.id AND redirecturi_id = r.id
  );
