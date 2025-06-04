ALTER TABLE users ADD COLUMN is_premium BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE users u
SET is_premium = TRUE
WHERE EXISTS (
    SELECT 1 FROM payments p
    WHERE p.user_id = u.id AND p.status = 'PAID'
);
