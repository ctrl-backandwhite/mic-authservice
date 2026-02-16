-- Insertar cliente OIDC en las tablas existentes
-- @formatter:off
--liquibase formatted sql

--changeset authservice:1.4_insert_grant_types
INSERT INTO grant_types (name, enabled) VALUES 
('authorization_code', true),
('refresh_token', true)
ON CONFLICT (name) DO NOTHING;

--changeset authservice:1.4_insert_scopes
INSERT INTO scopes (name, unique_name, description, enabled) VALUES 
('OpenID', 'openid', 'OpenID Connect scope', true),
('Profile', 'profile', 'Profile scope', true)
ON CONFLICT (unique_name) DO NOTHING;

--changeset authservice:1.4_insert_redirect_uris
INSERT INTO redirect_uris (name, value, enabled) VALUES 
('localhost admin', 'http://localhost:4200/admin', true),
('localhost callback', 'http://localhost:4200/auth/callback', true),
('production admin', 'https://webapp-production-68d2.up.railway.app/admin', true),
('production callback', 'https://webapp-production-68d2.up.railway.app/auth/callback', true),
('oauth debugger', 'https://oauthdebugger.com/debug', true)
ON CONFLICT (value) DO NOTHING;

--changeset authservice:1.4_insert_oauth_client
INSERT INTO oauth_clients (client_id, client_secret) 
VALUES ('oidc-client', '$2a$10$7JGmcJOzzYT3WqKpXy1z1OPST9/PgBkqquzi.Ss7KIUgO2t0jKMm2')
ON CONFLICT (client_id) DO NOTHING;

--changeset authservice:1.4_insert_oauth_client_grant_types
INSERT INTO oauthclient_granttypes (oauthclient_id, granttype_id)
SELECT c.id, g.id FROM oauth_clients c, grant_types g 
WHERE c.client_id = 'oidc-client' 
  AND g.name IN ('authorization_code', 'refresh_token')
  AND NOT EXISTS (
    SELECT 1 FROM oauthclient_granttypes 
    WHERE oauthclient_id = c.id AND granttype_id = g.id
  );

--changeset authservice:1.4_insert_oauth_client_scopes
INSERT INTO oauthclient_scopes (oauthclient_id, scope_id)
SELECT c.id, s.id FROM oauth_clients c, scopes s 
WHERE c.client_id = 'oidc-client' 
  AND s.unique_name IN ('openid', 'profile')
  AND NOT EXISTS (
    SELECT 1 FROM oauthclient_scopes 
    WHERE oauthclient_id = c.id AND scope_id = s.id
  );

--changeset authservice:1.4_insert_oauth_client_redirect_uris
INSERT INTO oauthclient_redirecturis (oauthclient_id, redirecturi_id)
SELECT c.id, r.id FROM oauth_clients c, redirect_uris r 
WHERE c.client_id = 'oidc-client' 
  AND r.value IN ('http://localhost:4200/admin', 'http://localhost:4200/auth/callback', 
                    'https://webapp-production-68d2.up.railway.app/admin', 
                    'https://webapp-production-68d2.up.railway.app/auth/callback',
                    'https://oauthdebugger.com/debug')
  AND NOT EXISTS (
    SELECT 1 FROM oauthclient_redirecturis 
    WHERE oauthclient_id = c.id AND redirecturi_id = r.id
  );
