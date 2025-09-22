-- Create flights table
CREATE TABLE flights (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(20) UNIQUE NOT NULL,
    available_seats INT NOT NULL DEFAULT 0,
    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL,
    price DOUBLE NOT NULL DEFAULT 0.00,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    aircraft_id BIGINT NOT NULL,
    route_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aircraft_id) REFERENCES aircrafts(id) ON DELETE CASCADE,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_flights_number ON flights(flight_number);
CREATE INDEX idx_flights_aircraft ON flights(aircraft_id);
CREATE INDEX idx_flights_route ON flights(route_id);
CREATE INDEX idx_flights_departure ON flights(departure_time);
