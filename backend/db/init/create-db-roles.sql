-- Create PostgreSQL users for role-based connections
CREATE ROLE admin_user WITH LOGIN PASSWORD 'admin_pass';
CREATE ROLE manager_user WITH LOGIN PASSWORD 'manager_pass';
CREATE ROLE customer_user WITH LOGIN PASSWORD 'customer_pass';

-- Grant access to schema
ALTER DEFAULT PRIVILEGES IN SCHEMA public
  GRANT ALL ON TABLES TO admin_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE ON TABLES TO manager_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
  GRANT SELECT ON TABLES TO customer_user;