-- Actualizar redirect URIs de localhost para base path /nexa-auth
UPDATE redirect_uris
SET value = 'http://localhost:4200/nexa-auth/auth/callback'
WHERE value = 'http://localhost:4200/auth/callback';

UPDATE redirect_uris
SET value = 'http://localhost:4200/nexa-auth/admin'
WHERE value = 'http://localhost:4200/admin';
