-- ──────────────────────────────────────────────────────────────────────────────
-- Add mobile app redirect URI for ecommerce-client (nx036://auth/callback)
-- Required for React Native PKCE OAuth2 flow
-- ──────────────────────────────────────────────────────────────────────────────

--changeset authservice:1.17_insert_mobile_redirect_uri
INSERT INTO redirect_uris (name, value, enabled) VALUES
('ecommerce-mobile', 'nx036://auth/callback', true)
ON CONFLICT (value) DO NOTHING;

--changeset authservice:1.17_link_mobile_redirect_to_ecommerce_client
INSERT INTO oauthclient_redirecturis (oauthclient_id, redirecturi_id)
SELECT c.id, r.id
FROM oauth_clients c, redirect_uris r
WHERE c.client_id = 'ecommerce-client'
  AND r.value = 'nx036://auth/callback'
  AND NOT EXISTS (
    SELECT 1 FROM oauthclient_redirecturis
    WHERE oauthclient_id = c.id AND redirecturi_id = r.id
  );
