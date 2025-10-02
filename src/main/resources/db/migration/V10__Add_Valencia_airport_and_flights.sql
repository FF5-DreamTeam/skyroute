-- Valencia airport
INSERT INTO airports (code, city, image_url) VALUES
('VLC', 'Valencia', 'https://res.cloudinary.com/skyroute/image/upload/v1759304495/Beste-wijken-Valencia-2024_tgylqc.jpg');

-- Routes from Valencia 
INSERT INTO routes (origin_id, destination_id)
SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='VLC' AND d.code='JFK'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='VLC' AND d.code='LHR'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='VLC' AND d.code='CDG'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='VLC' AND d.code='NRT'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='VLC' AND d.code='DXB'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='VLC' AND d.code='HKG'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='VLC' AND d.code='FRA'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='VLC' AND d.code='MAD'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='VLC' AND d.code='FCO'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='VLC' AND d.code='AMS';

-- Flights from Valencia 
-- VLC to JFK
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR011', 150, '2025-03-15 09:00:00', '2025-03-15 15:30:00', 899.99, TRUE, 1, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='JFK';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR012', 150, '2025-04-20 14:30:00', '2025-04-20 16:45:00', 899.99, TRUE, 1, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='JFK';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR013', 150, '2025-05-25 18:15:00', '2025-05-26 00:30:00', 899.99, TRUE, 1, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='JFK';

-- VLC to LHR
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR014', 200, '2025-03-18 11:30:00', '2025-03-18 13:45:00', 199.99, TRUE, 2, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='LHR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR015', 200, '2025-04-22 16:00:00', '2025-04-22 18:15:00', 199.99, TRUE, 2, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='LHR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR016', 200, '2025-05-28 20:45:00', '2025-05-28 23:00:00', 199.99, TRUE, 2, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='LHR';

-- VLC to CDG
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR017', 300, '2025-03-21 08:15:00', '2025-03-21 10:30:00', 149.99, TRUE, 3, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='CDG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR018', 300, '2025-04-25 13:45:00', '2025-04-25 16:00:00', 149.99, TRUE, 3, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='CDG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR019', 300, '2025-05-30 17:30:00', '2025-05-30 19:45:00', 149.99, TRUE, 3, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='CDG';

-- VLC to NRT
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR020', 250, '2025-03-24 10:00:00', '2025-03-25 08:15:00', 1299.99, TRUE, 4, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='NRT';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR021', 250, '2025-04-28 15:30:00', '2025-04-29 13:45:00', 1299.99, TRUE, 4, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='NRT';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR022', 250, '2025-06-02 19:15:00', '2025-06-03 17:30:00', 1299.99, TRUE, 4, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='NRT';

-- VLC to DXB
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR023', 180, '2025-03-27 12:45:00', '2025-03-27 22:30:00', 799.99, TRUE, 5, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='DXB';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR024', 180, '2025-05-01 18:00:00', '2025-05-02 03:45:00', 799.99, TRUE, 5, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='DXB';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR025', 180, '2025-06-05 21:30:00', '2025-06-06 07:15:00', 799.99, TRUE, 5, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='DXB';

-- VLC to HKG
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR026', 220, '2025-03-30 14:15:00', '2025-03-31 08:30:00', 1199.99, TRUE, 6, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='HKG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR027', 220, '2025-05-04 20:45:00', '2025-05-05 15:00:00', 1199.99, TRUE, 6, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='HKG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR028', 220, '2025-06-08 23:00:00', '2025-06-09 17:15:00', 1199.99, TRUE, 6, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='HKG';

-- VLC to FRA
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR029', 400, '2025-04-02 09:30:00', '2025-04-02 12:45:00', 299.99, TRUE, 7, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='FRA';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR030', 400, '2025-05-07 15:15:00', '2025-05-07 18:30:00', 299.99, TRUE, 7, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='FRA';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR031', 400, '2025-06-11 17:45:00', '2025-06-11 21:00:00', 299.99, TRUE, 7, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='FRA';

-- VLC to MAD
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR032', 350, '2025-04-05 11:00:00', '2025-04-05 12:15:00', 199.99, TRUE, 8, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='MAD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR033', 350, '2025-05-10 16:30:00', '2025-05-10 17:45:00', 199.99, TRUE, 8, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='MAD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR034', 350, '2025-06-14 19:00:00', '2025-06-14 20:15:00', 199.99, TRUE, 8, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='MAD';

-- VLC to FCO
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR035', 190, '2025-04-08 13:45:00', '2025-04-08 15:30:00', 299.99, TRUE, 9, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='FCO';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR036', 190, '2025-05-13 18:15:00', '2025-05-13 20:00:00', 299.99, TRUE, 9, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='FCO';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR037', 190, '2025-06-17 21:30:00', '2025-06-17 23:15:00', 299.99, TRUE, 9, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='FCO';

-- VLC to AMS
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR038', 280, '2025-04-11 10:30:00', '2025-04-11 13:15:00', 399.99, TRUE, 10, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='AMS';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR039', 280, '2025-05-16 15:45:00', '2025-05-16 18:30:00', 399.99, TRUE, 10, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='AMS';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR040', 280, '2025-06-20 20:00:00', '2025-06-20 22:45:00', 399.99, TRUE, 10, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VLC' AND d.code='AMS';

-- Return routes to Valencia
INSERT INTO routes (origin_id, destination_id)
SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='JFK' AND d.code='VLC'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='LHR' AND d.code='VLC'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='CDG' AND d.code='VLC'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='NRT' AND d.code='VLC'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='DXB' AND d.code='VLC'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='HKG' AND d.code='VLC'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='FRA' AND d.code='VLC'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='MAD' AND d.code='VLC'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='FCO' AND d.code='VLC'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='AMS' AND d.code='VLC';

-- Return flights to Valencia
-- JFK to VLC
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR041', 150, '2025-03-16 10:00:00', '2025-03-16 16:30:00', 899.99, TRUE, 1, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='JFK' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR042', 150, '2025-04-21 15:30:00', '2025-04-21 22:00:00', 899.99, TRUE, 1, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='JFK' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR043', 150, '2025-05-26 19:15:00', '2025-05-27 01:45:00', 899.99, TRUE, 1, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='JFK' AND d.code='VLC';

-- LHR to VLC
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR044', 200, '2025-03-19 12:30:00', '2025-03-19 14:45:00', 199.99, TRUE, 2, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR045', 200, '2025-04-23 17:00:00', '2025-04-23 19:15:00', 199.99, TRUE, 2, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR046', 200, '2025-05-29 21:45:00', '2025-05-30 00:00:00', 199.99, TRUE, 2, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='VLC';

-- CDG to VLC
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR047', 300, '2025-03-22 09:15:00', '2025-03-22 11:30:00', 149.99, TRUE, 3, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CDG' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR048', 300, '2025-04-26 14:45:00', '2025-04-26 17:00:00', 149.99, TRUE, 3, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CDG' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR049', 300, '2025-05-31 18:30:00', '2025-05-31 20:45:00', 149.99, TRUE, 3, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CDG' AND d.code='VLC';

-- NRT to VLC
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR050', 250, '2025-03-25 11:00:00', '2025-03-26 09:15:00', 1299.99, TRUE, 4, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='NRT' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR051', 250, '2025-04-29 16:30:00', '2025-04-30 14:45:00', 1299.99, TRUE, 4, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='NRT' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR052', 250, '2025-06-03 20:15:00', '2025-06-04 18:30:00', 1299.99, TRUE, 4, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='NRT' AND d.code='VLC';

-- DXB to VLC
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR053', 180, '2025-03-28 13:45:00', '2025-03-28 23:30:00', 799.99, TRUE, 5, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='DXB' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR054', 180, '2025-05-02 19:00:00', '2025-05-03 04:45:00', 799.99, TRUE, 5, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='DXB' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR055', 180, '2025-06-06 22:30:00', '2025-06-07 08:15:00', 799.99, TRUE, 5, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='DXB' AND d.code='VLC';

-- HKG to VLC
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR056', 220, '2025-03-31 15:15:00', '2025-04-01 09:30:00', 1199.99, TRUE, 6, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HKG' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR057', 220, '2025-05-05 21:45:00', '2025-05-06 16:00:00', 1199.99, TRUE, 6, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HKG' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR058', 220, '2025-06-09 00:00:00', '2025-06-09 18:15:00', 1199.99, TRUE, 6, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HKG' AND d.code='VLC';

-- FRA to VLC
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR059', 400, '2025-04-03 10:30:00', '2025-04-03 13:45:00', 299.99, TRUE, 7, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FRA' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR060', 400, '2025-05-08 16:15:00', '2025-05-08 19:30:00', 299.99, TRUE, 7, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FRA' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR061', 400, '2025-06-12 18:45:00', '2025-06-12 22:00:00', 299.99, TRUE, 7, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FRA' AND d.code='VLC';

-- MAD to VLC
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR062', 350, '2025-04-06 12:00:00', '2025-04-06 13:15:00', 199.99, TRUE, 8, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR063', 350, '2025-05-11 17:30:00', '2025-05-11 18:45:00', 199.99, TRUE, 8, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR064', 350, '2025-06-15 20:00:00', '2025-06-15 21:15:00', 199.99, TRUE, 8, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='VLC';

-- FCO to VLC
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR065', 190, '2025-04-09 14:45:00', '2025-04-09 16:30:00', 299.99, TRUE, 9, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FCO' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR066', 190, '2025-05-14 19:15:00', '2025-05-14 21:00:00', 299.99, TRUE, 9, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FCO' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR067', 190, '2025-06-18 22:30:00', '2025-06-19 00:15:00', 299.99, TRUE, 9, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FCO' AND d.code='VLC';

-- AMS to VLC 
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR068', 280, '2025-04-12 11:30:00', '2025-04-12 14:15:00', 399.99, TRUE, 10, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='AMS' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR069', 280, '2025-05-17 16:45:00', '2025-05-17 19:30:00', 399.99, TRUE, 10, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='AMS' AND d.code='VLC';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR070', 280, '2025-06-21 21:00:00', '2025-06-21 23:45:00', 399.99, TRUE, 10, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='AMS' AND d.code='VLC';
