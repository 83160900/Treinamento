-- Limpa e insere o usuário admin na tabela users do projeto Fitness
DELETE FROM users WHERE email = 'admin';
INSERT INTO users (id, email, password, name, role, active) 
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'admin', 'admin', 'Administrador', 'ADMIN', true)
ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password, name = EXCLUDED.name, role = EXCLUDED.role, active = EXCLUDED.active;
