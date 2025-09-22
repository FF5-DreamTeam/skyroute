-- Create airports table
CREATE TABLE airports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    city VARCHAR(100) NOT NULL,
    image_url VARCHAR(255)
);

-- Create index on code for faster lookups
CREATE INDEX idx_airports_code ON airports(code);
