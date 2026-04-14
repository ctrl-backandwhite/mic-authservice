DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_group_roles_role_id') THEN
        ALTER TABLE group_roles ADD CONSTRAINT fk_group_roles_role_id FOREIGN KEY (role_id) REFERENCES roles;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_group_roles_group_id') THEN
        ALTER TABLE group_roles ADD CONSTRAINT fk_group_roles_group_id FOREIGN KEY (group_id) REFERENCES groups;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_oauthclient_granttypes_granttype_id') THEN
        ALTER TABLE oauthclient_granttypes ADD CONSTRAINT fk_oauthclient_granttypes_granttype_id FOREIGN KEY (granttype_id) REFERENCES grant_types;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_oauthclient_granttypes_oauthclient_id') THEN
        ALTER TABLE oauthclient_granttypes ADD CONSTRAINT fk_oauthclient_granttypes_oauthclient_id FOREIGN KEY (oauthclient_id) REFERENCES oauth_clients;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_oauthclient_redirecturis_redirecturi_id') THEN
        ALTER TABLE oauthclient_redirecturis ADD CONSTRAINT fk_oauthclient_redirecturis_redirecturi_id FOREIGN KEY (redirecturi_id) REFERENCES redirect_uris;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_oauthclient_redirecturis_oauthclient_id') THEN
        ALTER TABLE oauthclient_redirecturis ADD CONSTRAINT fk_oauthclient_redirecturis_oauthclient_id FOREIGN KEY (oauthclient_id) REFERENCES oauth_clients;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_oauthclient_scopes_scope_id') THEN
        ALTER TABLE oauthclient_scopes ADD CONSTRAINT fk_oauthclient_scopes_scope_id FOREIGN KEY (scope_id) REFERENCES scopes;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_oauthclient_scopes_oauthclient_id') THEN
        ALTER TABLE oauthclient_scopes ADD CONSTRAINT fk_oauthclient_scopes_oauthclient_id FOREIGN KEY (oauthclient_id) REFERENCES oauth_clients;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_groups_group_id') THEN
        ALTER TABLE user_groups ADD CONSTRAINT fk_user_groups_group_id FOREIGN KEY (group_id) REFERENCES groups;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_groups_user_id') THEN
        ALTER TABLE user_groups ADD CONSTRAINT fk_user_groups_user_id FOREIGN KEY (user_id) REFERENCES users;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_roles_role_id') THEN
        ALTER TABLE user_roles ADD CONSTRAINT fk_user_roles_role_id FOREIGN KEY (role_id) REFERENCES roles;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_roles_user_id') THEN
        ALTER TABLE user_roles ADD CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES users;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_scopes_scope_id') THEN
        ALTER TABLE user_scopes ADD CONSTRAINT fk_user_scopes_scope_id FOREIGN KEY (scope_id) REFERENCES scopes;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_scopes_user_id') THEN
        ALTER TABLE user_scopes ADD CONSTRAINT fk_user_scopes_user_id FOREIGN KEY (user_id) REFERENCES users;
    END IF;
END $$;