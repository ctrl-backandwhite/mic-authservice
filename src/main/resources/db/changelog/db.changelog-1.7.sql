--changeset authservice:1.9_add_password_reset_token_to_users
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_reset_token VARCHAR(64) UNIQUE;

--changeset authservice:1.9_create_index_password_reset_token
CREATE INDEX IF NOT EXISTS idx_users_password_reset_token ON users(password_reset_token);

--changeset authservice:1.9_add_password_reset_token_expiry
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_reset_token_expiry TIMESTAMPTZ;
