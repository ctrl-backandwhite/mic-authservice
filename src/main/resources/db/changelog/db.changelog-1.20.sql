-- ──────────────────────────────────────────────────────────────────────────────
-- Remove refresh_token grant from ecommerce-client.
--
-- Spring Authorization Server requires client authentication for the
-- refresh_token grant (client_secret_basic/post). Public clients
-- (ClientAuthenticationMethod.NONE) can only authenticate via PKCE on the
-- authorization_code grant — not on refresh_token. The SPA would fail with
-- [invalid_client] and clear its tokens.
--
-- Instead, the SPA does a silent re-authorization via /oauth2/authorize when
-- the access token expires; the user's SSO session with the auth service
-- returns a new code without re-prompting for credentials.
-- ──────────────────────────────────────────────────────────────────────────────

--changeset authservice:1.20_remove_ecommerce_refresh_token_grant
DELETE FROM oauthclient_granttypes
WHERE oauthclient_id = (SELECT id FROM oauth_clients WHERE client_id = 'ecommerce-client')
  AND granttype_id = (SELECT id FROM grant_types WHERE name = 'refresh_token');
