-- Add password reset fields to users table
ALTER TABLE users ADD COLUMN password_reset_token VARCHAR(255);
ALTER TABLE users ADD COLUMN password_reset_token_expires_at TIMESTAMP;

-- Create index on password reset token for faster lookups
CREATE INDEX idx_users_password_reset_token ON users(password_reset_token);
