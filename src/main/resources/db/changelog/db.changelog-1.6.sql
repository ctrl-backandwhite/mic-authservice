--changeset authservice:1.8_add_activation_token_to_users
ALTER TABLE users ADD COLUMN IF NOT EXISTS activation_token VARCHAR(64) UNIQUE;

--changeset authservice:1.8_create_index_activation_token
CREATE INDEX IF NOT EXISTS idx_users_activation_token ON users(activation_token);

--changeset authservice:1.8_add_activation_token_expiry
ALTER TABLE users ADD COLUMN IF NOT EXISTS activation_token_expiry TIMESTAMPTZ;
