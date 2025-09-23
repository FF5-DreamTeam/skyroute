-- Create aircrafts table
CREATE TABLE aircrafts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    capacity INT NOT NULL,
    model VARCHAR(100) NOT NULL,
    manufacturer VARCHAR(100) NOT NULL
);

-- Create index on model for faster lookups
CREATE INDEX idx_aircrafts_model ON aircrafts(model);
