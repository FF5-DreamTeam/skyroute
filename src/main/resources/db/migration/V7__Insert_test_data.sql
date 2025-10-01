-- Insert users
INSERT INTO users (first_name, last_name, birth_date, user_img_url, phone_number, email, password, role, created_at, updated_at) VALUES
('John', 'Administrator', '1985-03-15', 'https://res.cloudinary.com/skyroute/image/upload/v1759253377/9b56c003d032ee25521915f222270108_cropped_510x510_ynqlhf.webp', '+345676780998', 'admin@skyroute.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', '2024-01-15 09:30:00', '2024-01-15 09:30:00'),
('Alice', 'Johnson', '1990-07-22', 'https://res.cloudinary.com/skyroute/image/upload/v1759253376/memi-klev-club-m9i9-p-memi-smeshnoi-kot-ulibaetsya-3_amphwo.jpg', '+34321345654', 'alice.johnson@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', '2024-02-10 14:20:00', '2024-02-10 14:20:00'),
('Bob', 'Smith', '1988-11-08', 'https://res.cloudinary.com/skyroute/image/upload/v1759253416/close-up-portrait-beautiful-cat_23-2149214419_bsgt45.avif', '+34567432345', 'bob.smith@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', '2024-02-15 16:45:00', '2024-02-15 16:45:00');

-- Insert airports
INSERT INTO airports (code, city, image_url) VALUES
('JFK', 'New York', 'https://res.cloudinary.com/skyroute/image/upload/v1759015110/photo-1485871981521-5b1fd3805eee_etlxk4.jpg'),
('LAX', 'Los Angeles', 'https://res.cloudinary.com/skyroute/image/upload/v1758527421/Getty_515070156_EDITORIALONLY_LosAngeles_HollywoodBlvd_Web72DPI_0_xogwu6.jpg'),
('LHR', 'London', 'https://res.cloudinary.com/skyroute/image/upload/v1758529627/big-ben-westminster-bridge-sunset-london-uk_s6rdrj.jpg'),
('CDG', 'Paris', 'https://res.cloudinary.com/skyroute/image/upload/v1758529860/98.jpg_sj0koc.webp'),
('NRT', 'Tokyo', 'https://res.cloudinary.com/skyroute/image/upload/v1758529969/premium_photo-1661914240950-b0124f20a5c1_r0bnys.jpg'),
('DXB', 'Dubai', 'https://res.cloudinary.com/skyroute/image/upload/v1758530092/Dubai-iStock-JandaliPhoto_cvh7oj.jpg'),
('SIN', 'Singapore', 'https://res.cloudinary.com/skyroute/image/upload/v1758530176/singapore-4-1600x900.jpeg_eadq0q.webp'),
('HKG', 'Hong Kong', 'https://res.cloudinary.com/skyroute/image/upload/v1758530242/e358adfb-city-23190-164c80b6af6_gskk5g.jpg'),
('FRA', 'Frankfurt', 'https://res.cloudinary.com/skyroute/image/upload/v1758530326/Qu%C3%A9-ver-en-Frankfurt_drstjw.jpg'),
('MAD', 'Madrid', 'https://res.cloudinary.com/skyroute/image/upload/v1758530433/photo-1539037116277-4db20889f2d4_hqolhb.jpg'),
('FCO', 'Rome', 'https://res.cloudinary.com/skyroute/image/upload/v1758530490/premium_photo-1661963952208-2db3512ef3de_g9dmzt.jpg'),
('BCN', 'Barcelona', 'https://res.cloudinary.com/skyroute/image/upload/v1758530554/hotel-arc-la-rambla-monumentos-edificios-visitar-barrio-eixample-barcelona-1_tkygay.webp'),
('AMS', 'Amsterdam', 'https://res.cloudinary.com/skyroute/image/upload/v1758531596/80160_fullimage_rondvaartboot_vaart_onder_brug_door_met_mooie_wolkenlucht_illusion-x_via_pixabay_1150x663_438x353_cmm68o.jpg'),
('ZUR', 'Zurich', 'https://res.cloudinary.com/skyroute/image/upload/v1758531872/building-7616202-Header_Mobile_mmdfvc.webp'),
('VIE', 'Vienna', 'https://res.cloudinary.com/skyroute/image/upload/v1758531928/vienna-skyline_snnpfz.jpg'),
('IST', 'Istanbul', 'https://res.cloudinary.com/skyroute/image/upload/v1758532838/p0gzbrt0_ucocou.jpg'),
('DOH', 'Doha', 'https://res.cloudinary.com/skyroute/image/upload/v1758532917/istockphoto-912738544-612x612_p1iicm.jpg'),
('ICN', 'Seoul', 'https://res.cloudinary.com/skyroute/image/upload/v1758533136/Namsan-Tower-during-autumn-in-Seoul-South-Korea-1244x700_k5ga7p.jpg'),
('PEK', 'Beijing', 'https://res.cloudinary.com/skyroute/image/upload/v1758533495/Beijing_etalfe.jpg'),
('SYD', 'Sydney', 'https://res.cloudinary.com/skyroute/image/upload/v1758533559/sydney-things-to-do-800x500-Sydney-Opera-House-8_quxdmw.jpg');

-- Insert aircrafts
INSERT INTO aircrafts (capacity, model, manufacturer) VALUES
(150, 'Boeing 737-800', 'Boeing'),
(200, 'Airbus A320', 'Airbus'),
(300, 'Boeing 777-300ER', 'Boeing'),
(250, 'Airbus A350-900', 'Airbus'),
(180, 'Boeing 737 MAX 8', 'Boeing'),
(220, 'Airbus A321', 'Airbus'),
(400, 'Boeing 747-8', 'Boeing'),
(350, 'Airbus A380', 'Airbus'),
(190, 'Boeing 787-9 Dreamliner', 'Boeing'),
(280, 'Airbus A330-900', 'Airbus');

-- Insert routes
INSERT INTO routes (origin_id, destination_id) VALUES
(1, 2),  -- JFK to LAX
(2, 3),  -- LAX to LHR
(3, 4),  -- LHR to CDG
(4, 5),  -- CDG to NRT
(5, 1),  -- NRT to JFK
(6, 7),  -- DXB to SIN
(7, 8),  -- SIN to HKG
(8, 9),  -- HKG to FRA
(9, 10), -- FRA to MAD
(10, 11); -- MAD to FCO

-- Insert flights (mostly future dates, few past for testing)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id, created_at, updated_at) VALUES
('SR001', 150, '2025-01-15 08:00:00', '2025-01-15 11:30:00', 299.99, TRUE, 1, 1, '2025-01-10 10:15:00', '2025-01-10 10:15:00'),
('SR002', 200, '2025-02-20 14:30:00', '2025-02-21 06:45:00', 899.99, TRUE, 2, 2, '2025-02-15 11:30:00', '2025-02-15 11:30:00'),
('SR003', 300, '2025-12-15 10:15:00', '2025-12-15 12:30:00', 199.99, TRUE, 3, 3, '2025-09-01 09:45:00', '2025-09-01 09:45:00'),
('SR004', 250, '2025-12-20 16:45:00', '2025-12-21 08:20:00', 1299.99, TRUE, 4, 4, '2025-09-01 14:20:00', '2025-09-01 14:20:00'),
('SR005', 180, '2025-12-25 20:00:00', '2025-12-26 14:30:00', 1599.99, TRUE, 5, 5, '2025-09-01 16:10:00', '2025-09-01 16:10:00'),
('SR006', 220, '2025-12-30 06:30:00', '2025-12-30 12:15:00', 799.99, TRUE, 6, 6, '2025-09-01 08:20:00', '2025-09-01 08:20:00'),
('SR007', 400, '2026-01-05 18:45:00', '2026-01-06 14:20:00', 1899.99, TRUE, 7, 7, '2025-09-01 12:30:00', '2025-09-01 12:30:00'),
('SR008', 350, '2026-01-10 09:15:00', '2026-01-10 15:30:00', 1199.99, TRUE, 8, 8, '2025-09-01 14:45:00', '2025-09-01 14:45:00'),
('SR009', 190, '2026-01-15 13:20:00', '2026-01-15 19:45:00', 699.99, TRUE, 9, 9, '2025-09-01 16:15:00', '2025-09-01 16:15:00'),
('SR010', 280, '2026-01-20 07:00:00', '2026-01-20 11:30:00', 999.99, TRUE, 10, 10, '2025-09-01 18:30:00', '2025-09-01 18:30:00');

-- Insert bookings (mostly future flights, few past for history)
INSERT INTO booking (booking_number, booked_seats, total_price, booking_status, user_id, flight_id, created_at, updated_at) VALUES
('BK001', 2, 599.98, 'CONFIRMED', 2, 1, '2025-01-10 15:30:00', '2025-01-10 15:30:00'), -- Alice books 2 seats for past flight SR001
('BK002', 1, 899.99, 'CONFIRMED', 2, 2, '2025-02-15 09:15:00', '2025-02-15 09:15:00'), -- Alice books 1 seat for past flight SR002
('BK003', 3, 599.97, 'CONFIRMED', 3, 3, '2025-09-15 11:45:00', '2025-09-15 11:45:00'), -- Bob books 3 seats for future flight SR003
('BK004', 1, 1299.99, 'CANCELLED', 3, 4, '2025-09-20 14:20:00', '2025-09-20 14:20:00'), -- Bob cancels booking for future flight SR004
('BK005', 2, 3199.98, 'CONFIRMED', 3, 5, '2025-09-25 16:10:00', '2025-09-25 16:10:00'), -- Bob books 2 seats for future flight SR005
('BK006', 1, 799.99, 'CONFIRMED', 2, 6, '2025-09-30 10:20:00', '2025-09-30 10:20:00'), -- Alice books 1 seat for future flight SR006
('BK007', 4, 7599.96, 'CONFIRMED', 3, 7, '2025-10-05 14:15:00', '2025-10-05 14:15:00'), -- Bob books 4 seats for future flight SR007
('BK008', 2, 2399.98, 'CONFIRMED', 2, 8, '2025-10-10 16:30:00', '2025-10-10 16:30:00'), -- Alice books 2 seats for future flight SR008
('BK009', 1, 699.99, 'CANCELLED', 3, 9, '2025-10-15 09:45:00', '2025-10-15 09:45:00'), -- Bob cancels booking for future flight SR009
('BK010', 3, 2999.97, 'CONFIRMED', 2, 10, '2025-10-20 12:00:00', '2025-10-20 12:00:00'); -- Alice books 3 seats for future flight SR010

-- Insert passenger names for bookings
INSERT INTO booking_passenger_names (booking_id, passenger_names) VALUES
(1, 'Alice Johnson'), -- Alice's booking
(1, 'Michael Johnson'), -- Alice's companion
(2, 'Alice Johnson'), -- Alice's second booking
(3, 'Bob Smith'), -- Bob's booking
(3, 'Sarah Smith'), -- Bob's companion
(3, 'Emma Smith'), -- Bob's second companion
(4, 'Bob Smith'), -- Bob's cancelled booking
(5, 'Bob Smith'), -- Bob's third booking
(5, 'Lisa Smith'), -- Bob's companion
(6, 'Alice Johnson'), -- Alice's third booking
(7, 'Bob Smith'), -- Bob's fourth booking
(7, 'Sarah Smith'), -- Bob's companion
(7, 'Emma Smith'), -- Bob's second companion
(7, 'Lisa Smith'), -- Bob's third companion
(8, 'Alice Johnson'), -- Alice's fourth booking
(8, 'Michael Johnson'), -- Alice's companion
(9, 'Bob Smith'), -- Bob's cancelled booking
(10, 'Alice Johnson'), -- Alice's fifth booking
(10, 'Michael Johnson'), -- Alice's companion
(10, 'Jennifer Johnson'); -- Alice's second companion

-- Insert passenger birth dates for bookings
INSERT INTO booking_passenger_birth_dates (booking_id, passenger_birth_dates) VALUES
(1, '1990-07-22'), -- Alice Johnson
(1, '1988-05-10'), -- Michael Johnson
(2, '1990-07-22'), -- Alice Johnson
(3, '1988-11-08'), -- Bob Smith
(3, '1992-03-15'), -- Sarah Smith
(3, '2015-09-20'), -- Emma Smith
(4, '1988-11-08'), -- Bob Smith
(5, '1988-11-08'), -- Bob Smith
(5, '1990-12-03'), -- Lisa Smith
(6, '1990-07-22'), -- Alice Johnson
(7, '1988-11-08'), -- Bob Smith
(7, '1992-03-15'), -- Sarah Smith
(7, '2015-09-20'), -- Emma Smith
(7, '1990-12-03'), -- Lisa Smith
(8, '1990-07-22'), -- Alice Johnson
(8, '1988-05-10'), -- Michael Johnson
(9, '1988-11-08'), -- Bob Smith
(10, '1990-07-22'), -- Alice Johnson
(10, '1988-05-10'), -- Michael Johnson
(10, '1995-04-18'); -- Jennifer Johnson
