-- Expand seed data for 2026 with more airports, routes, and flights

-- New airports
INSERT INTO airports (code, city, image_url) VALUES
('SFO', 'San Francisco', 'https://res.cloudinary.com/skyroute/image/upload/v1759140396/48403a5b516bde2c40c68d8c37cce29b0051f7ff-1600x1066_no9ndc.jpg'),
('MIA', 'Miami', 'https://res.cloudinary.com/skyroute/image/upload/v1759140590/Get-the-Best-Pictures-of-the-Miami-Skyline_ova0ip.jpg'),
('YYZ', 'Toronto', 'https://res.cloudinary.com/skyroute/image/upload/v1759140682/285736_669077a41c251_raizro.jpg'),
('GRU', 'Sao Paulo', 'https://res.cloudinary.com/skyroute/image/upload/v1759140754/1200_ir4fzt.jpg'),
('EZE', 'Buenos Aires', 'https://res.cloudinary.com/skyroute/image/upload/v1759140861/m_4bb363f747b09b6_srqtnv.jpg'),
('SCL', 'Santiago', 'https://res.cloudinary.com/skyroute/image/upload/v1759140920/0023040d_2000x1331-1030x685_lagnf6.jpg'),
('LIM', 'Lima', 'https://res.cloudinary.com/skyroute/image/upload/v1759141018/49863184666_fd4f8e0c37_b_hxestv.jpg'),
('BOG', 'Bogota', 'https://res.cloudinary.com/skyroute/image/upload/v1759141076/turismo-bogota_-reconocida-como-destino-de-negocios-lider-sudamerica.png_ckfy9t.jpg'),
('MEX', 'Mexico City', 'https://res.cloudinary.com/skyroute/image/upload/v1759141158/NC-Immigration-Insights-hero-images-7-1_c0rzfr.jpg'),
('ORD', 'Chicago', 'https://res.cloudinary.com/skyroute/image/upload/v1759141222/543370-chicago_ljhsuc.jpg'),
('DFW', 'Dallas', 'https://res.cloudinary.com/skyroute/image/upload/v1759141289/dallas-texas-compressed_kwyv71.jpg'),
('SEA', 'Seattle', 'https://res.cloudinary.com/skyroute/image/upload/v1759141388/photo0jpg_rcvoby.jpg'),
('BOS', 'Boston', 'https://res.cloudinary.com/skyroute/image/upload/v1759141496/boston-quartieri_duy0ri.jpg'),
('IAD', 'Washington', 'https://res.cloudinary.com/skyroute/image/upload/v1759141604/guia-washington_uuwxfd.jpg'),
('ATH', 'Athens', 'https://res.cloudinary.com/skyroute/image/upload/v1759141658/20220531-Athens-Hub-_-Nick-N-A-Shutterstock-ed_xigrxo.jpg'),
('OSL', 'Oslo', 'https://res.cloudinary.com/skyroute/image/upload/v1759141750/oslo-norway-parliament-building-image_vpsu4l.jpg'),
('ARN', 'Stockholm', 'https://res.cloudinary.com/skyroute/image/upload/v1759141809/Stockholm-Sweden-1_gghp72.jpg'),
('CPH', 'Copenhagen', 'https://res.cloudinary.com/skyroute/image/upload/v1759141877/copenhagen_fishing_boats_vp0kgp.jpg'),
('HEL', 'Helsinki', 'https://res.cloudinary.com/skyroute/image/upload/v1759141943/Helsinki-1_ktcahi.jpg'),
('PRG', 'Prague', 'https://res.cloudinary.com/skyroute/image/upload/v1759142002/1123pom-3-1_gi4ucv.jpg');

-- New routes
INSERT INTO routes (origin_id, destination_id)
SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='SFO' AND d.code='MIA'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='MIA' AND d.code='SFO'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='ORD' AND d.code='SFO'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='SFO' AND d.code='ORD'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='YYZ' AND d.code='LHR'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='ATH' AND d.code='MAD'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='ARN' AND d.code='CPH'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='CPH' AND d.code='OSL'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='HEL' AND d.code='VIE'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='PRG' AND d.code='ZUR';

-- New flights

-- SFO -> MIA (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR101', 180, '2026-02-10 07:30:00', '2026-02-10 15:10:00', 279.99, TRUE, 1, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id
WHERE o.code='SFO' AND d.code='MIA';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR102', 200, '2026-03-15 08:00:00', '2026-03-15 15:40:00', 299.99, TRUE, 2, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id
WHERE o.code='SFO' AND d.code='MIA';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR103', 160, '2026-05-20 09:15:00', '2026-05-20 16:55:00', 339.99, TRUE, 3, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id
WHERE o.code='SFO' AND d.code='MIA';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR104', 220, '2026-07-05 06:45:00', '2026-07-05 14:25:00', 319.99, TRUE, 4, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id
WHERE o.code='SFO' AND d.code='MIA';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR105', 190, '2026-09-12 10:30:00', '2026-09-12 18:05:00', 289.99, TRUE, 5, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id
WHERE o.code='SFO' AND d.code='MIA';

-- MIA -> SFO (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR106', 170, '2026-02-12 11:00:00', '2026-02-12 18:40:00', 289.99, TRUE, 6, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id
WHERE o.code='MIA' AND d.code='SFO';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR107', 210, '2026-04-18 07:20:00', '2026-04-18 14:55:00', 309.99, TRUE, 7, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MIA' AND d.code='SFO';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR108', 180, '2026-06-22 13:10:00', '2026-06-22 20:45:00', 329.99, TRUE, 8, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MIA' AND d.code='SFO';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR109', 200, '2026-08-30 06:00:00', '2026-08-30 13:35:00', 279.99, TRUE, 9, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MIA' AND d.code='SFO';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR110', 150, '2026-11-05 09:45:00', '2026-11-05 17:20:00', 299.49, TRUE, 10, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MIA' AND d.code='SFO';

-- ORD -> SFO (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR111', 180, '2026-01-14 07:00:00', '2026-01-14 10:05:00', 239.99, TRUE, 1, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ORD' AND d.code='SFO';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR112', 200, '2026-03-03 15:30:00', '2026-03-03 18:40:00', 249.99, TRUE, 2, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ORD' AND d.code='SFO';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR113', 160, '2026-05-09 12:20:00', '2026-05-09 15:25:00', 259.99, TRUE, 3, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ORD' AND d.code='SFO';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR114', 220, '2026-07-21 06:40:00', '2026-07-21 09:45:00', 269.99, TRUE, 4, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ORD' AND d.code='SFO';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR115', 190, '2026-10-02 08:15:00', '2026-10-02 11:20:00', 279.99, TRUE, 5, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ORD' AND d.code='SFO';

-- SFO -> ORD (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR116', 170, '2026-01-20 06:20:00', '2026-01-20 12:30:00', 239.49, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SFO' AND d.code='ORD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR117', 210, '2026-03-28 09:00:00', '2026-03-28 15:10:00', 249.49, TRUE, 7, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SFO' AND d.code='ORD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR118', 180, '2026-06-01 13:45:00', '2026-06-01 19:55:00', 259.49, TRUE, 8, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SFO' AND d.code='ORD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR119', 200, '2026-08-18 07:35:00', '2026-08-18 13:45:00', 269.49, TRUE, 9, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SFO' AND d.code='ORD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR120', 150, '2026-11-12 10:25:00', '2026-11-12 16:35:00', 279.49, TRUE, 10, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SFO' AND d.code='ORD';

-- YYZ -> LHR (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR121', 280, '2026-02-05 18:00:00', '2026-02-06 06:30:00', 549.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='YYZ' AND d.code='LHR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR122', 260, '2026-04-12 19:30:00', '2026-04-13 08:00:00', 569.99, TRUE, 2, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='YYZ' AND d.code='LHR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR123', 300, '2026-06-10 20:00:00', '2026-06-11 08:30:00', 599.99, TRUE, 3, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='YYZ' AND d.code='LHR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR124', 250, '2026-08-16 17:15:00', '2026-08-17 05:45:00', 579.99, TRUE, 4, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='YYZ' AND d.code='LHR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR125', 270, '2026-10-22 16:50:00', '2026-10-23 05:20:00', 559.99, TRUE, 5, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='YYZ' AND d.code='LHR';

-- ATH -> MAD (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR126', 180, '2026-03-03 08:10:00', '2026-03-03 11:30:00', 199.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ATH' AND d.code='MAD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR127', 200, '2026-05-07 07:35:00', '2026-05-07 10:55:00', 209.99, TRUE, 7, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ATH' AND d.code='MAD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR128', 160, '2026-07-19 06:50:00', '2026-07-19 10:10:00', 219.99, TRUE, 8, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ATH' AND d.code='MAD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR129', 220, '2026-09-25 09:20:00', '2026-09-25 12:40:00', 229.99, TRUE, 9, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ATH' AND d.code='MAD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR130', 190, '2026-12-03 11:40:00', '2026-12-03 15:05:00', 239.99, TRUE, 10, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ATH' AND d.code='MAD';

-- ARN -> CPH (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR131', 150, '2026-01-11 08:20:00', '2026-01-11 09:35:00', 129.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ARN' AND d.code='CPH';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR132', 160, '2026-03-14 14:10:00', '2026-03-14 15:25:00', 139.99, TRUE, 2, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ARN' AND d.code='CPH';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR133', 170, '2026-06-02 18:40:00', '2026-06-02 19:55:00', 119.99, TRUE, 3, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ARN' AND d.code='CPH';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR134', 160, '2026-09-09 07:50:00', '2026-09-09 09:05:00', 149.99, TRUE, 4, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ARN' AND d.code='CPH';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR135', 180, '2026-11-17 12:30:00', '2026-11-17 13:45:00', 139.49, TRUE, 5, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ARN' AND d.code='CPH';

-- CPH -> OSL (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR136', 150, '2026-02-08 10:05:00', '2026-02-08 11:25:00', 99.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CPH' AND d.code='OSL';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR137', 160, '2026-04-20 16:15:00', '2026-04-20 17:35:00', 109.99, TRUE, 7, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CPH' AND d.code='OSL';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR138', 170, '2026-06-28 08:25:00', '2026-06-28 09:45:00', 119.99, TRUE, 8, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CPH' AND d.code='OSL';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR139', 160, '2026-09-14 13:35:00', '2026-09-14 14:55:00', 129.99, TRUE, 9, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CPH' AND d.code='OSL';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR140', 180, '2026-12-21 18:45:00', '2026-12-21 20:05:00', 139.99, TRUE, 10, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CPH' AND d.code='OSL';

-- HEL -> VIE (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR141', 150, '2026-02-02 07:10:00', '2026-02-02 09:40:00', 189.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HEL' AND d.code='VIE';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR142', 160, '2026-04-08 12:20:00', '2026-04-08 14:50:00', 199.99, TRUE, 2, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HEL' AND d.code='VIE';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR143', 170, '2026-06-16 15:30:00', '2026-06-16 18:00:00', 209.99, TRUE, 3, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HEL' AND d.code='VIE';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR144', 160, '2026-09-01 09:40:00', '2026-09-01 12:10:00', 219.99, TRUE, 4, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HEL' AND d.code='VIE';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR145', 180, '2026-11-10 17:50:00', '2026-11-10 20:20:00', 229.99, TRUE, 5, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HEL' AND d.code='VIE';

-- PRG -> ZUR (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR146', 180, '2026-01-09 08:00:00', '2026-01-09 09:20:00', 159.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='PRG' AND d.code='ZUR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR147', 160, '2026-03-22 11:30:00', '2026-03-22 12:50:00', 169.99, TRUE, 7, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='PRG' AND d.code='ZUR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR148', 150, '2026-06-05 16:10:00', '2026-06-05 17:30:00', 179.99, TRUE, 8, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='PRG' AND d.code='ZUR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR149', 170, '2026-09-18 07:40:00', '2026-09-18 09:00:00', 189.99, TRUE, 9, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='PRG' AND d.code='ZUR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR150', 180, '2026-12-28 13:20:00', '2026-12-28 14:40:00', 199.99, TRUE, 10, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='PRG' AND d.code='ZUR';

-- 4) New Flights for existing routes

-- JFK -> LAX
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR151', 180, '2026-01-08 08:00:00', '2026-01-08 11:30:00', 309.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='JFK' AND d.code='LAX';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR152', 200, '2026-03-05 09:15:00', '2026-03-05 12:45:00', 329.99, TRUE, 2, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='JFK' AND d.code='LAX';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR153', 160, '2026-05-12 07:30:00', '2026-05-12 11:00:00', 289.99, TRUE, 3, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='JFK' AND d.code='LAX';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR154', 220, '2026-08-03 10:20:00', '2026-08-03 13:50:00', 349.99, TRUE, 4, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='JFK' AND d.code='LAX';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR155', 190, '2026-10-27 12:45:00', '2026-10-27 16:15:00', 319.99, TRUE, 5, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='JFK' AND d.code='LAX';

-- LAX -> LHR
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR156', 200, '2026-02-14 14:30:00', '2026-02-15 07:00:00', 949.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LAX' AND d.code='LHR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR157', 220, '2026-04-22 16:10:00', '2026-04-23 08:40:00', 979.99, TRUE, 7, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LAX' AND d.code='LHR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR158', 180, '2026-06-18 20:20:00', '2026-06-19 12:50:00', 899.99, TRUE, 8, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LAX' AND d.code='LHR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR159', 240, '2026-09-07 18:45:00', '2026-09-08 11:15:00', 929.99, TRUE, 9, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LAX' AND d.code='LHR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR160', 260, '2026-11-29 22:10:00', '2026-11-30 14:40:00', 959.99, TRUE, 10, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LAX' AND d.code='LHR';

-- LHR -> CDG
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR161', 160, '2026-01-19 09:10:00', '2026-01-19 11:25:00', 159.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='CDG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR162', 170, '2026-03-08 13:40:00', '2026-03-08 15:55:00', 169.99, TRUE, 2, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='CDG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR163', 180, '2026-05-21 08:00:00', '2026-05-21 10:15:00', 179.99, TRUE, 3, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='CDG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR164', 150, '2026-07-30 17:25:00', '2026-07-30 19:40:00', 189.99, TRUE, 4, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='CDG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR165', 160, '2026-10-11 06:45:00', '2026-10-11 08:55:00', 199.99, TRUE, 5, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='CDG';

-- CDG -> NRT
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR166', 250, '2026-02-02 12:00:00', '2026-02-03 05:20:00', 1299.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CDG' AND d.code='NRT';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR167', 260, '2026-04-17 15:40:00', '2026-04-18 09:00:00', 1249.99, TRUE, 7, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CDG' AND d.code='NRT';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR168', 270, '2026-06-24 18:10:00', '2026-06-25 11:30:00', 1199.99, TRUE, 8, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CDG' AND d.code='NRT';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR169', 240, '2026-09-03 21:30:00', '2026-09-04 14:00:00', 1349.99, TRUE, 9, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CDG' AND d.code='NRT';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR170', 230, '2026-12-15 08:50:00', '2026-12-16 01:20:00', 1399.99, TRUE, 10, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CDG' AND d.code='NRT';

-- NRT -> JFK
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR171', 200, '2026-01-26 10:00:00', '2026-01-26 20:00:00', 1099.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='NRT' AND d.code='JFK';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR172', 220, '2026-03-14 12:30:00', '2026-03-14 22:30:00', 1049.99, TRUE, 2, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='NRT' AND d.code='JFK';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR173', 240, '2026-06-07 16:45:00', '2026-06-08 02:45:00', 999.99, TRUE, 3, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='NRT' AND d.code='JFK';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR174', 260, '2026-09-19 20:15:00', '2026-09-20 06:15:00', 949.99, TRUE, 4, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='NRT' AND d.code='JFK';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR175', 180, '2026-11-23 09:35:00', '2026-11-23 19:35:00', 899.99, TRUE, 5, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='NRT' AND d.code='JFK';

-- DXB -> SIN
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR176', 220, '2026-02-07 06:20:00', '2026-02-07 15:50:00', 499.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='DXB' AND d.code='SIN';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR177', 240, '2026-04-04 08:10:00', '2026-04-04 17:40:00', 529.99, TRUE, 7, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='DXB' AND d.code='SIN';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR178', 260, '2026-06-26 10:05:00', '2026-06-26 19:35:00', 559.99, TRUE, 8, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='DXB' AND d.code='SIN';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR179', 200, '2026-09-08 12:00:00', '2026-09-08 21:30:00', 579.99, TRUE, 9, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='DXB' AND d.code='SIN';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR180', 180, '2026-12-01 14:15:00', '2026-12-01 23:45:00', 599.99, TRUE, 10, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='DXB' AND d.code='SIN';

-- SIN -> HKG
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR181', 200, '2026-01-13 09:00:00', '2026-01-13 12:30:00', 219.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SIN' AND d.code='HKG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR182', 210, '2026-03-09 11:10:00', '2026-03-09 14:40:00', 229.99, TRUE, 2, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SIN' AND d.code='HKG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR183', 220, '2026-06-14 13:20:00', '2026-06-14 16:50:00', 239.99, TRUE, 3, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SIN' AND d.code='HKG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR184', 180, '2026-09-22 15:30:00', '2026-09-22 19:00:00', 249.99, TRUE, 4, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SIN' AND d.code='HKG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR185', 190, '2026-12-19 17:40:00', '2026-12-19 21:10:00', 259.99, TRUE, 5, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SIN' AND d.code='HKG';

-- HKG -> FRA
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR186', 240, '2026-02-06 22:00:00', '2026-02-07 07:30:00', 749.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HKG' AND d.code='FRA';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR187', 220, '2026-04-01 23:20:00', '2026-04-02 08:50:00', 769.99, TRUE, 7, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HKG' AND d.code='FRA';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR188', 260, '2026-06-20 21:10:00', '2026-06-21 06:40:00', 789.99, TRUE, 8, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HKG' AND d.code='FRA';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR189', 200, '2026-09-10 20:30:00', '2026-09-11 06:00:00', 729.99, TRUE, 9, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HKG' AND d.code='FRA';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR190', 180, '2026-12-08 19:50:00', '2026-12-09 05:20:00', 709.99, TRUE, 10, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HKG' AND d.code='FRA';

-- FRA -> MAD
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR191', 180, '2026-01-17 07:40:00', '2026-01-17 10:00:00', 199.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FRA' AND d.code='MAD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR192', 170, '2026-03-26 12:20:00', '2026-03-26 14:40:00', 189.99, TRUE, 2, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FRA' AND d.code='MAD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR193', 160, '2026-06-03 16:35:00', '2026-06-03 18:55:00', 179.99, TRUE, 3, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FRA' AND d.code='MAD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR194', 200, '2026-09-12 09:10:00', '2026-09-12 11:30:00', 169.99, TRUE, 4, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FRA' AND d.code='MAD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR195', 210, '2026-11-30 14:50:00', '2026-11-30 17:10:00', 159.99, TRUE, 5, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FRA' AND d.code='MAD';

-- MAD -> FCO
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR196', 150, '2026-02-09 06:30:00', '2026-02-09 08:30:00', 149.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='FCO';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR197', 160, '2026-04-15 10:45:00', '2026-04-15 12:45:00', 159.99, TRUE, 7, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='FCO';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR198', 170, '2026-06-27 13:20:00', '2026-06-27 15:20:00', 169.99, TRUE, 8, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='FCO';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR199', 180, '2026-09-04 16:55:00', '2026-09-04 18:55:00', 179.99, TRUE, 9, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='FCO';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR200', 190, '2026-12-13 19:40:00', '2026-12-13 21:40:00', 189.99, TRUE, 10, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='FCO';

-- 5) Reverse routes 

-- Add reverse routes (avoid duplicates via unique constraint)
INSERT INTO routes (origin_id, destination_id)
SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='LAX' AND d.code='JFK'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='LHR' AND d.code='LAX'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='CDG' AND d.code='LHR'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='NRT' AND d.code='CDG'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='JFK' AND d.code='NRT'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='SIN' AND d.code='DXB'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='HKG' AND d.code='SIN'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='FRA' AND d.code='HKG'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='MAD' AND d.code='FRA'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='FCO' AND d.code='MAD';

-- Add reverse routes for new V9 pairs
INSERT INTO routes (origin_id, destination_id)
SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='LHR' AND d.code='YYZ'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='MAD' AND d.code='ATH'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='CPH' AND d.code='ARN'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='OSL' AND d.code='CPH'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='VIE' AND d.code='HEL'
UNION ALL SELECT o.id, d.id FROM airports o JOIN airports d ON 1=1 WHERE o.code='ZUR' AND d.code='PRG';

-- LHR -> YYZ (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR251', 260, '2026-02-09 12:30:00', '2026-02-09 15:20:00', 569.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='YYZ';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR252', 240, '2026-04-16 14:10:00', '2026-04-16 17:00:00', 589.99, TRUE, 2, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='YYZ';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR253', 280, '2026-06-12 16:50:00', '2026-06-12 19:40:00', 609.99, TRUE, 3, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='YYZ';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR254', 220, '2026-09-04 19:20:00', '2026-09-04 22:10:00', 579.99, TRUE, 4, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='YYZ';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR255', 200, '2026-11-21 21:40:00', '2026-11-22 00:30:00', 559.99, TRUE, 5, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='YYZ';

-- MAD -> ATH (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR256', 180, '2026-01-28 08:05:00', '2026-01-28 11:30:00', 219.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='ATH';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR257', 170, '2026-03-19 07:35:00', '2026-03-19 11:00:00', 229.99, TRUE, 7, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='ATH';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR258', 160, '2026-06-08 06:50:00', '2026-06-08 10:15:00', 239.99, TRUE, 8, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='ATH';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR259', 200, '2026-09-01 09:20:00', '2026-09-01 12:45:00', 249.99, TRUE, 9, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='ATH';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR260', 190, '2026-12-14 11:40:00', '2026-12-14 15:05:00', 259.99, TRUE, 10, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='ATH';

-- CPH -> ARN (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR261', 150, '2026-01-07 08:30:00', '2026-01-07 09:45:00', 129.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CPH' AND d.code='ARN';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR262', 160, '2026-03-16 14:15:00', '2026-03-16 15:30:00', 139.99, TRUE, 2, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CPH' AND d.code='ARN';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR263', 170, '2026-06-03 18:35:00', '2026-06-03 19:50:00', 119.99, TRUE, 3, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CPH' AND d.code='ARN';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR264', 160, '2026-09-08 07:55:00', '2026-09-08 09:10:00', 149.99, TRUE, 4, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CPH' AND d.code='ARN';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR265', 180, '2026-11-18 12:35:00', '2026-11-18 13:50:00', 139.49, TRUE, 5, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CPH' AND d.code='ARN';

-- OSL -> CPH (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR266', 150, '2026-02-06 10:10:00', '2026-02-06 11:30:00', 99.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='OSL' AND d.code='CPH';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR267', 160, '2026-04-18 16:20:00', '2026-04-18 17:40:00', 109.99, TRUE, 7, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='OSL' AND d.code='CPH';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR268', 170, '2026-06-30 08:30:00', '2026-06-30 09:50:00', 119.99, TRUE, 8, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='OSL' AND d.code='CPH';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR269', 160, '2026-09-16 13:40:00', '2026-09-16 15:00:00', 129.99, TRUE, 9, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='OSL' AND d.code='CPH';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR270', 180, '2026-12-23 18:50:00', '2026-12-23 20:10:00', 139.99, TRUE, 10, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='OSL' AND d.code='CPH';

-- VIE -> HEL (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR271', 150, '2026-02-03 07:20:00', '2026-02-03 09:50:00', 189.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VIE' AND d.code='HEL';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR272', 160, '2026-04-11 12:30:00', '2026-04-11 15:00:00', 199.99, TRUE, 2, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VIE' AND d.code='HEL';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR273', 170, '2026-06-19 15:40:00', '2026-06-19 18:10:00', 209.99, TRUE, 3, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VIE' AND d.code='HEL';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR274', 160, '2026-09-07 09:50:00', '2026-09-07 12:20:00', 219.99, TRUE, 4, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VIE' AND d.code='HEL';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR275', 180, '2026-11-15 18:00:00', '2026-11-15 20:30:00', 229.99, TRUE, 5, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='VIE' AND d.code='HEL';

-- ZUR -> PRG (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR276', 180, '2026-01-13 08:10:00', '2026-01-13 09:30:00', 159.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ZUR' AND d.code='PRG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR277', 160, '2026-03-24 11:40:00', '2026-03-24 13:00:00', 169.99, TRUE, 7, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ZUR' AND d.code='PRG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR278', 150, '2026-06-07 16:20:00', '2026-06-07 17:40:00', 179.99, TRUE, 8, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ZUR' AND d.code='PRG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR279', 170, '2026-09-21 07:50:00', '2026-09-21 09:10:00', 189.99, TRUE, 9, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ZUR' AND d.code='PRG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR280', 180, '2026-12-27 13:30:00', '2026-12-27 14:50:00', 199.99, TRUE, 10, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='ZUR' AND d.code='PRG';

-- LAX -> JFK (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR201', 200, '2026-01-06 09:15:00', '2026-01-06 17:45:00', 299.99, TRUE, 1, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LAX' AND d.code='JFK';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR202', 180, '2026-03-02 12:30:00', '2026-03-02 21:00:00', 319.99, TRUE, 2, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LAX' AND d.code='JFK';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR203', 220, '2026-05-14 07:50:00', '2026-05-14 16:20:00', 289.99, TRUE, 3, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LAX' AND d.code='JFK';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR204', 190, '2026-08-11 15:05:00', '2026-08-11 23:35:00', 339.99, TRUE, 4, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LAX' AND d.code='JFK';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR205', 210, '2026-11-07 20:40:00', '2026-11-08 05:10:00', 309.99, TRUE, 5, r.id
FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LAX' AND d.code='JFK';

-- LHR -> LAX (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR206', 230, '2026-02-03 12:10:00', '2026-02-03 20:40:00', 889.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='LAX';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR207', 210, '2026-04-09 14:20:00', '2026-04-09 22:50:00', 919.99, TRUE, 7, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='LAX';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR208', 240, '2026-06-27 16:30:00', '2026-06-27 01:00:00', 949.99, TRUE, 8, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='LAX';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR209', 200, '2026-09-16 18:40:00', '2026-09-17 03:10:00', 879.99, TRUE, 9, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='LAX';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR210', 220, '2026-12-05 10:55:00', '2026-12-05 19:25:00', 899.99, TRUE, 10, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='LHR' AND d.code='LAX';

-- CDG -> LHR (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR211', 150, '2026-01-22 08:00:00', '2026-01-22 08:55:00', 149.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CDG' AND d.code='LHR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR212', 160, '2026-03-11 10:15:00', '2026-03-11 11:10:00', 159.99, TRUE, 2, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CDG' AND d.code='LHR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR213', 170, '2026-05-24 12:30:00', '2026-05-24 13:25:00', 169.99, TRUE, 3, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CDG' AND d.code='LHR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR214', 160, '2026-08-02 14:45:00', '2026-08-02 15:40:00', 179.99, TRUE, 4, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CDG' AND d.code='LHR';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR215', 180, '2026-10-20 17:00:00', '2026-10-20 17:55:00', 189.99, TRUE, 5, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='CDG' AND d.code='LHR';

-- NRT -> CDG (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR216', 240, '2026-02-18 21:10:00', '2026-02-19 06:10:00', 1199.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='NRT' AND d.code='CDG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR217', 220, '2026-04-25 23:30:00', '2026-04-26 08:30:00', 1249.99, TRUE, 7, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='NRT' AND d.code='CDG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR218', 260, '2026-07-03 19:45:00', '2026-07-04 04:45:00', 1149.99, TRUE, 8, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='NRT' AND d.code='CDG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR219', 200, '2026-09-27 20:55:00', '2026-09-28 05:55:00', 1099.99, TRUE, 9, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='NRT' AND d.code='CDG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR220', 180, '2026-12-18 22:20:00', '2026-12-19 07:20:00', 1049.99, TRUE, 10, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='NRT' AND d.code='CDG';

-- JFK -> NRT (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR221', 220, '2026-01-29 09:40:00', '2026-01-30 00:20:00', 1199.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='JFK' AND d.code='NRT';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR222', 200, '2026-03-21 11:55:00', '2026-03-22 02:35:00', 1149.99, TRUE, 2, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='JFK' AND d.code='NRT';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR223', 240, '2026-06-15 13:10:00', '2026-06-16 03:50:00', 1099.99, TRUE, 3, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='JFK' AND d.code='NRT';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR224', 260, '2026-09-01 15:25:00', '2026-09-02 06:05:00', 1049.99, TRUE, 4, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='JFK' AND d.code='NRT';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR225', 180, '2026-11-26 17:40:00', '2026-11-27 08:20:00', 999.99, TRUE, 5, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='JFK' AND d.code='NRT';

-- SIN -> DXB (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR226', 200, '2026-02-04 07:30:00', '2026-02-04 11:00:00', 469.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SIN' AND d.code='DXB';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR227', 210, '2026-04-06 09:45:00', '2026-04-06 13:15:00', 489.99, TRUE, 7, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SIN' AND d.code='DXB';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR228', 220, '2026-06-11 12:00:00', '2026-06-11 15:30:00', 509.99, TRUE, 8, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SIN' AND d.code='DXB';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR229', 190, '2026-08-20 14:15:00', '2026-08-20 17:45:00', 529.99, TRUE, 9, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SIN' AND d.code='DXB';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR230', 180, '2026-11-09 16:30:00', '2026-11-09 20:00:00', 549.99, TRUE, 10, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='SIN' AND d.code='DXB';

-- HKG -> SIN (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR231', 200, '2026-01-10 10:00:00', '2026-01-10 13:30:00', 209.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HKG' AND d.code='SIN';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR232', 210, '2026-03-13 12:15:00', '2026-03-13 15:45:00', 219.99, TRUE, 2, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HKG' AND d.code='SIN';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR233', 220, '2026-06-19 14:30:00', '2026-06-19 18:00:00', 229.99, TRUE, 3, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HKG' AND d.code='SIN';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR234', 190, '2026-09-06 16:45:00', '2026-09-06 20:15:00', 239.99, TRUE, 4, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HKG' AND d.code='SIN';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR235', 180, '2026-12-11 18:00:00', '2026-12-11 21:30:00', 249.99, TRUE, 5, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='HKG' AND d.code='SIN';

-- FRA -> HKG (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR236', 240, '2026-02-01 21:30:00', '2026-02-02 10:00:00', 739.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FRA' AND d.code='HKG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR237', 220, '2026-03-29 20:10:00', '2026-03-30 08:40:00', 759.99, TRUE, 7, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FRA' AND d.code='HKG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR238', 260, '2026-06-08 19:00:00', '2026-06-09 07:30:00', 779.99, TRUE, 8, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FRA' AND d.code='HKG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR239', 200, '2026-09-15 18:40:00', '2026-09-16 07:10:00', 699.99, TRUE, 9, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FRA' AND d.code='HKG';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR240', 180, '2026-12-04 17:20:00', '2026-12-05 06:50:00', 679.99, TRUE, 10, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FRA' AND d.code='HKG';

-- MAD -> FRA (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR241', 180, '2026-01-21 06:40:00', '2026-01-21 09:05:00', 169.99, TRUE, 1, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='FRA';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR242', 170, '2026-03-25 11:20:00', '2026-03-25 13:45:00', 179.99, TRUE, 2, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='FRA';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR243', 160, '2026-06-04 15:35:00', '2026-06-04 18:00:00', 189.99, TRUE, 3, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='FRA';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR244', 200, '2026-09-09 08:10:00', '2026-09-09 10:35:00', 199.99, TRUE, 4, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='FRA';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR245', 210, '2026-11-27 13:50:00', '2026-11-27 16:15:00', 209.99, TRUE, 5, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='MAD' AND d.code='FRA';

-- FCO -> MAD (5 flights)
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR246', 150, '2026-02-16 07:15:00', '2026-02-16 09:15:00', 139.99, TRUE, 6, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FCO' AND d.code='MAD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR247', 160, '2026-04-19 11:30:00', '2026-04-19 13:30:00', 149.99, TRUE, 7, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FCO' AND d.code='MAD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR248', 170, '2026-06-30 14:05:00', '2026-06-30 16:05:00', 159.99, TRUE, 8, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FCO' AND d.code='MAD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR249', 180, '2026-09-13 17:40:00', '2026-09-13 19:40:00', 169.99, TRUE, 9, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FCO' AND d.code='MAD';
INSERT INTO flights (flight_number, available_seats, departure_time, arrival_time, price, available, aircraft_id, route_id)
SELECT 'SR250', 190, '2026-12-22 20:25:00', '2026-12-22 22:25:00', 179.99, TRUE, 10, r.id FROM routes r JOIN airports o ON r.origin_id=o.id JOIN airports d ON r.destination_id=d.id WHERE o.code='FCO' AND d.code='MAD';


