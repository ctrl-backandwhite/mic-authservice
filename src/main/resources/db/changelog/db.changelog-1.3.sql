-- Insert default GUEST role
INSERT INTO roles (id, name, unique_name, description, enabled) 
VALUES (4, 'Guest', 'ROLE_GUEST', 'Guest role', true)
ON CONFLICT DO NOTHING;
