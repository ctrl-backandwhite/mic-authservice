-- ============================================================
-- Session tracking table + session revoke columns on users
-- ============================================================

--changeset authservice:1.14_create_user_sessions_table
CREATE TABLE IF NOT EXISTS user_sessions (
    id          BIGSERIAL       PRIMARY KEY,
    user_id     BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_id  VARCHAR(64)     NOT NULL UNIQUE,
    authorization_id VARCHAR(64),
    device_info VARCHAR(255),
    ip_address  VARCHAR(45),
    user_agent  TEXT,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    last_active_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    revoked     BOOLEAN         NOT NULL DEFAULT FALSE,
    revoked_at  TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_user_sessions_user_id    ON user_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_user_sessions_session_id ON user_sessions(session_id);

--changeset authservice:1.14_add_session_revoke_columns_to_users
ALTER TABLE users ADD COLUMN IF NOT EXISTS session_revoke_code        VARCHAR(6);
ALTER TABLE users ADD COLUMN IF NOT EXISTS session_revoke_code_expiry TIMESTAMPTZ;
ALTER TABLE users ADD COLUMN IF NOT EXISTS session_to_revoke          VARCHAR(64);
