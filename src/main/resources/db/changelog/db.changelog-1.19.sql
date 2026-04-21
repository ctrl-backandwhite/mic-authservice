-- ──────────────────────────────────────────────────────────────────────────────
-- Register gateway redirect URI for ecommerce-client.
-- The SPA runs behind the API gateway at http://localhost:9000, so the OAuth2
-- callback must also come through the gateway (not direct to Vite at :5174).
-- ──────────────────────────────────────────────────────────────────────────────

--changeset authservice:1.19_insert_ecommerce_gateway_redirect_uri
INSERT INTO redirect_uris (name, value, enabled) VALUES
('ecommerce-gateway', 'http://localhost:9000/auth/callback', true)
ON CONFLICT (value) DO NOTHING;

--changeset authservice:1.19_link_ecommerce_gateway_redirect_uri
INSERT INTO oauthclient_redirecturis (oauthclient_id, redirecturi_id)
SELECT c.id, r.id FROM oauth_clients c, redirect_uris r
WHERE c.client_id = 'ecommerce-client'
  AND r.value = 'http://localhost:9000/auth/callback'
  AND NOT EXISTS (
    SELECT 1 FROM oauthclient_redirecturis
    WHERE oauthclient_id = c.id AND redirecturi_id = r.id
  );
