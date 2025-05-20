-- Application-level roles table
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Join table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Insert default roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('MANAGER'), ('CUSTOMER');

-- Find the CUSTOMER role ID
WITH customer_role AS (
  SELECT id FROM roles WHERE name = 'CUSTOMER'
)

-- Insert a CUSTOMER role for every user who doesn't have one
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, cr.id
FROM users u, customer_role cr
WHERE NOT EXISTS (
  SELECT 1 FROM user_roles ur
  WHERE ur.user_id = u.id AND ur.role_id = cr.id
);