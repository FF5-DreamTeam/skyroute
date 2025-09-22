-- Create booking table
CREATE TABLE booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_number VARCHAR(50) UNIQUE NOT NULL,
    seats_booked INT NOT NULL DEFAULT 1,
    total_price DOUBLE NOT NULL,
    booking_status VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL,
    flight_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE
);

-- Create booking passenger names table
CREATE TABLE booking_passenger_names (
    booking_id BIGINT NOT NULL,
    passenger_names VARCHAR(100) NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE CASCADE
);

-- Create booking passenger birth dates table
CREATE TABLE booking_passenger_birth_dates (
    booking_id BIGINT NOT NULL,
    passenger_birth_dates DATE NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_booking_number ON booking(booking_number);
CREATE INDEX idx_booking_user ON booking(user_id);
CREATE INDEX idx_booking_flight ON booking(flight_id);
CREATE INDEX idx_booking_status ON booking(booking_status);
