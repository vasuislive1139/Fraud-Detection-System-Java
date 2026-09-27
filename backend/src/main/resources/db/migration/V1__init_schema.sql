CREATE TABLE roles (
    id VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255)
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

INSERT INTO roles (id, description) VALUES ('ROLE_ADMIN', 'Administrator with full system access');
INSERT INTO roles (id, description) VALUES ('ROLE_ANALYST', 'Fraud analyst with case investigation access');
INSERT INTO roles (id, description) VALUES ('ROLE_SYSTEM', 'System role for simulation APIs');

-- Add a default admin user. 
-- The password hash here is for 'admin123' using BCrypt (cost 10).
INSERT INTO users (id, username, password_hash, role_id) 
VALUES ('123e4567-e89b-12d3-a456-426614174000', 'admin', '$2a$10$wY.A.3Wv/rV3d8o8Z/5Lhe7D0F62H5F8Yp1aY2aQ2R/j4bK2u8mC.', 'ROLE_ADMIN');
