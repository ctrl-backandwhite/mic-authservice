--changeset authservice:1.8_insert_des_redirect_uri
INSERT INTO redirect_uris (name, value, enabled) VALUES
('web-auth-des callback', 'https://web-auth-des.up.railway.app/auth/callback', true)
ON CONFLICT (value) DO NOTHING;

--changeset authservice:1.8_link_des_redirect_uri_to_oidc_client
INSERT INTO oauthclient_redirecturis (oauthclient_id, redirecturi_id)
SELECT c.id, r.id FROM oauth_clients c, redirect_uris r
WHERE c.client_id = 'oidc-client'
  AND r.value = 'https://web-auth-des.up.railway.app/auth/callback'
  AND NOT EXISTS (
    SELECT 1 FROM oauthclient_redirecturis
    WHERE oauthclient_id = c.id AND redirecturi_id = r.id
  ); 


