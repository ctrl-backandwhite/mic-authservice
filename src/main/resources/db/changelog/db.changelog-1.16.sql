--changeset authservice:1.16_add_password_change_code_to_users
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_change_code VARCHAR(6);

--changeset authservice:1.16_add_password_change_code_expiry_to_users
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_change_code_expiry TIMESTAMPTZ;
