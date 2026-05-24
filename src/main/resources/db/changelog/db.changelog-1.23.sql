-- ──────────────────────────────────────────────────────────────────────────────
-- Seed: usuario administrador para pruebas E2E.
--
-- Crea (idempotentemente, vía WHERE NOT EXISTS — más robusto que
-- ON CONFLICT cuando los constraints unique pueden ser anónimos):
--   • Rol ROLE_ADMIN y ROLE_USER si no existen.
--   • Usuario admin@nx036.local / Admin123! con ROLE_ADMIN + ROLE_USER.
--   • Refresca el seed de role_permissions del changelog 1.21 (que apuntaba
--     erróneamente a unique_name='ADMIN' en vez de 'ROLE_ADMIN').
-- ──────────────────────────────────────────────────────────────────────────────

--changeset authservice:1.23_seed_admin_user splitStatements:false

-- 1. Roles base
INSERT INTO roles (name, unique_name, description, enabled)
SELECT 'Administrator', 'ROLE_ADMIN', 'Full administrative access', true
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE unique_name = 'ROLE_ADMIN');

INSERT INTO roles (name, unique_name, description, enabled)
SELECT 'User', 'ROLE_USER', 'Standard authenticated user', true
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE unique_name = 'ROLE_USER');

-- 2. Usuario admin (bcrypt('Admin123!') generado con rounds=10).
INSERT INTO users (nick_name, email, password, name, last_name, enabled,
                   account_non_expired, account_non_locked, credentials_non_expired,
                   created_at, updated_at)
SELECT 'admin', 'admin@nx036.local',
       '$2b$10$iQppA844WE4qhXpfhW6MjuIq.PZ1PKLMJPiFNDT/7y77VY9l7TQ1y',
       'Admin', 'NX036', true, true, true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@nx036.local');

-- 3. Asignar ROLE_ADMIN + ROLE_USER al admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE u.email = 'admin@nx036.local'
  AND r.unique_name IN ('ROLE_ADMIN', 'ROLE_USER')
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- 4. Grant-all de permisos al ROLE_ADMIN (fix del 1.21 que usaba 'ADMIN')
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.unique_name = 'ROLE_ADMIN'
  AND p.unique_name IN (
    'orders:read', 'orders:write', 'orders:cancel',
    'products:read', 'products:write', 'products:delete',
    'users:read', 'users:write', 'users:delete',
    'cms:write', 'returns:approve', 'analytics:view', 'rbac:manage'
  )
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
