INSERT INTO roles(name, description, created_at)
VALUES
('SUPER_ADMIN', 'System Administrator', CURRENT_TIMESTAMP),
('SALON_OWNER', 'Salon Owner', CURRENT_TIMESTAMP),
('STAFF', 'Salon Staff', CURRENT_TIMESTAMP),
('CUSTOMER', 'Customer', CURRENT_TIMESTAMP);

INSERT INTO permissions(code, description, created_at)
VALUES

-- USER
('user:create', 'Create user', CURRENT_TIMESTAMP),
('user:view', 'View user', CURRENT_TIMESTAMP),
('user:update', 'Update user', CURRENT_TIMESTAMP),
('user:delete', 'Delete user', CURRENT_TIMESTAMP),

-- SALON
('salon:create', 'Create salon', CURRENT_TIMESTAMP),
('salon:view', 'View salon', CURRENT_TIMESTAMP),
('salon:update', 'Update salon', CURRENT_TIMESTAMP);

INSERT INTO role_permissions(role_id, permission_id)
SELECT
    (SELECT id FROM roles WHERE name = 'SUPER_ADMIN'),
    id
FROM permissions;

INSERT INTO role_permissions(role_id, permission_id)
SELECT
    (SELECT id FROM roles WHERE name='SALON_OWNER'),
    id
FROM permissions
WHERE code IN (

'salon:create',
'salon:view',
'salon:update'
);
