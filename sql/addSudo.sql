-- Create a new user specifically for the application
-- Change 'secure_password' to a strong password of your choice
CREATE USER IF NOT EXISTS 'NEXT_SUDO'@'localhost' IDENTIFIED BY 'VerySecurePassword';

-- Grant all privileges ONLY on the travelplus database to this user
GRANT ALL PRIVILEGES ON NEXT.* TO 'NEXT_SUDO'@'localhost';

-- Apply the changes
FLUSH PRIVILEGES;

-- Verification (Optional)
-- SHOW GRANTS FOR 'NEXT_SUDO'@'localhost';