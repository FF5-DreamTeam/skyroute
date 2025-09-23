-- Create routes table
CREATE TABLE routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    origin_id BIGINT NOT NULL,
    destination_id BIGINT NOT NULL,
    FOREIGN KEY (origin_id) REFERENCES airports(id) ON DELETE CASCADE,
    FOREIGN KEY (destination_id) REFERENCES airports(id) ON DELETE CASCADE,
    CONSTRAINT unique_route UNIQUE (origin_id, destination_id)
);

-- Create indexes for better performance
CREATE INDEX idx_routes_origin ON routes(origin_id);
CREATE INDEX idx_routes_destination ON routes(destination_id);
