-- ============================================================
-- Seed the shared demo account for the hosted demo.
--
-- Credentials (advertised on the public demo page):
--   username: demo
--   password: demo-password-123
--
-- The hash below is BCrypt ($2y$, cost 10) of that password — Spring's
-- BCryptPasswordEncoder verifies $2y$ hashes. role=USER, enabled=TRUE.
-- ON CONFLICT keeps this idempotent if the username already exists.
-- ============================================================

INSERT INTO users (username, email, password_hash, role, enabled)
VALUES (
    'demo',
    'demo@example.com',
    '$2y$10$5fOV/FEiK5vroZchGANBwupQLjXapYQ.C837BF9kbpHKgH/dNh8ZW',
    'USER',
    TRUE
)
ON CONFLICT (username) DO NOTHING;
