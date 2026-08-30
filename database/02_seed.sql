-- ============================================================================
-- FixMate - Local Service Booking & Management Platform
-- File: 02_seed.sql
-- Description: Realistic Production Seed Data (Ujjain, Indore, Bhopal, Dewas)
-- ============================================================================

USE fixmate_db;

-- ----------------------------------------------------------------------------
-- 1. SEED USERS
-- Password for all accounts is: Password@123
-- BCrypt Hash: $2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC
-- ----------------------------------------------------------------------------

-- 1.1 Admin User (User 1)
INSERT INTO users (user_id, full_name, email, phone, password_hash, role, status) VALUES
(1, 'Platform Admin', 'admin123@fixmate.in', '+919999000001', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'ADMIN', 'ACTIVE');

-- 1.2 Customers (Users 2 to 21)
INSERT INTO users (user_id, full_name, email, phone, password_hash, role, status) VALUES
(2, 'Demo Customer', 'customer123@fixmate.in', '+919826011001', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(3, 'Priya Patel', 'priya.patel@gmail.com', '+919826011002', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(4, 'Rohan Verma', 'rohan.verma@gmail.com', '+919826011003', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(5, 'Ananya Joshi', 'ananya.joshi@gmail.com', '+919826011004', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(6, 'Vikram Malhotra', 'vikram.m@gmail.com', '+919826011005', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(7, 'Sneha Gupta', 'sneha.gupta@gmail.com', '+919826011006', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(8, 'Aditya Chouhan', 'aditya.c@gmail.com', '+919826011007', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(9, 'Pooja Tiwari', 'pooja.tiwari@gmail.com', '+919826011008', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(10, 'Manish Rathore', 'manish.r@gmail.com', '+919826011009', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(11, 'Neha Trivedi', 'neha.t@gmail.com', '+919826011010', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(12, 'Karan Solanki', 'karan.s@gmail.com', '+919826011011', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(13, 'Divya Agarwal', 'divya.a@gmail.com', '+919826011012', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(14, 'Gaurav Yadav', 'gaurav.y@gmail.com', '+919826011013', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(15, 'Meera Soni', 'meera.soni@gmail.com', '+919826011014', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(16, 'Suresh Jain', 'suresh.jain@gmail.com', '+919826011015', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(17, 'Kavita Shrivastava', 'kavita.s@gmail.com', '+919826011016', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(18, 'Deepak Dubey', 'deepak.dubey@gmail.com', '+919826011017', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(19, 'Ritu Parmar', 'ritu.parmar@gmail.com', '+919826011018', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(20, 'Harshita Pandey', 'harshita.p@gmail.com', '+919826011019', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE'),
(21, 'Nitin Mishra', 'nitin.mishra@gmail.com', '+919826011020', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'CUSTOMER', 'ACTIVE');

-- 1.3 Service Providers (Users 22 to 41)
INSERT INTO users (user_id, full_name, email, phone, password_hash, role, status) VALUES
(22, 'Demo Provider', 'provider123@fixmate.in', '+919826022001', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(23, 'Amit Verma', 'amit.cooling@fixmate.in', '+919826022002', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(24, 'Dinesh Malviya', 'dinesh.plumbing@fixmate.in', '+919826022003', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(25, 'Sunil Carpenter', 'sunil.carpenter@fixmate.in', '+919826022004', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(26, 'Santosh Yadav', 'santosh.cleaning@fixmate.in', '+919826022005', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(27, 'Mukesh Prajapati', 'mukesh.appliances@fixmate.in', '+919826022006', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(28, 'Vivek Chaurasia', 'vivek.techfix@fixmate.in', '+919826022007', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(29, 'Bhupendra Lodhi', 'bhupendra.paint@fixmate.in', '+919826022008', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(30, 'Chetan Bhati', 'chetan.pest@fixmate.in', '+919826022009', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(31, 'Deepak Sikarwar', 'deepak.electric@fixmate.in', '+919826022010', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(32, 'Gopal Baghel', 'gopal.acservice@fixmate.in', '+919826022011', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(33, 'Hemant Nagar', 'hemant.plumber@fixmate.in', '+919826022012', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(34, 'Inderjeet Singh', 'inder.woodworks@fixmate.in', '+919826022013', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(35, 'Jagdish Chauhan', 'jagdish.spark@fixmate.in', '+919826022014', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(36, 'Kailash Meena', 'kailash.clean@fixmate.in', '+919826022015', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(37, 'Laxman Dhangar', 'laxman.rorepair@fixmate.in', '+919826022016', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(38, 'Manoj Kushwaha', 'manoj.gadgets@fixmate.in', '+919826022017', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(39, 'Nandkishore Gour', 'nandu.painter@fixmate.in', '+919826022018', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE'),
(40, 'Prakash Gehlot', 'prakash.safehome@fixmate.in', '+919826022019', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'PENDING_VERIFICATION'),
(41, 'Radheshyam Kulmi', 'radheshyam.tech@fixmate.in', '+919826022020', '$2a$10$wT.fGevc/8g4gK9UoGg5U.k75yR45ZcE577n9M7r1bV5Uu7sD9yTC', 'PROVIDER', 'ACTIVE');

-- ----------------------------------------------------------------------------
-- 2. SEED CUSTOMERS
-- ----------------------------------------------------------------------------
INSERT INTO customers (customer_id, user_id, profile_image, total_bookings) VALUES
(1, 2, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80', 6),
(2, 3, 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80', 8),
(3, 4, 'https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=150&q=80', 4),
(4, 5, 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=150&q=80', 5),
(5, 6, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80', 7),
(6, 7, 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=150&q=80', 3),
(7, 8, 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80', 6),
(8, 9, 'https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=150&q=80', 5),
(9, 10, 'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=150&q=80', 4),
(10, 11, 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80', 6),
(11, 12, 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=150&q=80', 5),
(12, 13, 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=150&q=80', 4),
(13, 14, 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=150&q=80', 8),
(14, 15, 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=150&q=80', 6),
(15, 16, 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=150&q=80', 5),
(16, 17, 'https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=150&q=80', 4),
(17, 18, 'https://images.unsplash.com/photo-1513956589380-bad6acb9b9d4?auto=format&fit=crop&w=150&q=80', 7),
(18, 19, 'https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=150&q=80', 5),
(19, 20, 'https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?auto=format&fit=crop&w=150&q=80', 6),
(20, 21, 'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&w=150&q=80', 4);

-- ----------------------------------------------------------------------------
-- 3. SEED SERVICE PROVIDERS (Centered around Ujjain, with Indore, Bhopal, Dewas)
-- ----------------------------------------------------------------------------
INSERT INTO service_providers 
(provider_id, user_id, bio, experience_years, address, city, state, pincode, latitude, longitude, verification_status, id_proof_type, id_proof_number, rating_avg, rating_count, total_completed_jobs, wallet_balance)
VALUES
(1, 22, 'Govt certified master electrician with 8+ years specializing in residential wiring, short-circuit troubleshooting and MCB installs.', 8, '14, Freeganj Main Road', 'Ujjain', 'Madhya Pradesh', '456001', 23.1764720, 75.7885440, 'ACTIVE', 'AADHAAR', '671234981123', 4.90, 28, 35, 14250.00),
(2, 23, 'Expert AC mechanic specializing in inverter ACs, gas charging, copper piping, and deep chemical jet cleaning.', 6, '28, Mahakal Marg, Near Harsiddhi Gate', 'Ujjain', 'Madhya Pradesh', '456006', 23.1827550, 75.7681220, 'ACTIVE', 'AADHAAR', '881234981124', 4.85, 24, 30, 18900.00),
(3, 24, 'Complete sanitary, pipeline leakage, bathroom fitting and motor repair specialist with prompt 30-min doorstep arrival.', 7, '52, Nanakheda Colony, Sanwer Road', 'Ujjain', 'Madhya Pradesh', '456010', 23.1534120, 75.7941200, 'ACTIVE', 'AADHAAR', '991234981125', 4.75, 20, 26, 9800.00),
(4, 25, 'Custom woodwork, modern modular kitchen repairs, door locks, and furniture assembly with precision finish.', 10, '103, Madhav Nagar, Near Tower Chowk', 'Ujjain', 'Madhya Pradesh', '456001', 23.1691230, 75.7812340, 'ACTIVE', 'AADHAAR', '331234981126', 4.80, 18, 22, 11400.00),
(5, 26, 'Professional deep home cleaning, sofa shampooing, and kitchen sanitization with commercial grade machinery.', 5, '71, Rishi Nagar Extension', 'Ujjain', 'Madhya Pradesh', '456010', 23.1587410, 75.7796320, 'ACTIVE', 'AADHAAR', '441234981127', 4.70, 16, 20, 15600.00),
(6, 27, 'Washing machine, microwave, and refrigerator repair technician trained by LG and Samsung certified centers.', 9, '88, Sethi Nagar, Dewas Road', 'Ujjain', 'Madhya Pradesh', '456010', 23.1645120, 75.8012450, 'ACTIVE', 'AADHAAR', '551234981128', 4.90, 22, 28, 17200.00),
(7, 28, 'Chip-level laptop repair, Windows/Mac troubleshooting, SSD upgrades, and motherboard circuit fixes.', 6, '12, Vikram Marg, Freeganj', 'Ujjain', 'Madhya Pradesh', '456001', 23.1789120, 75.7854120, 'ACTIVE', 'AADHAAR', '771234981129', 4.85, 19, 25, 12800.00),
(8, 29, 'Interior wall painting, texture finish, waterproof putty, and Royal luxury sheen application.', 11, '34, Agar Road, Near Chimanganj Mandi', 'Ujjain', 'Madhya Pradesh', '456006', 23.2014120, 75.7896320, 'ACTIVE', 'AADHAAR', '221234981130', 4.65, 14, 18, 21000.00),
(9, 30, 'Herbal and chemical pest control for termites, bed bugs, and cockroaches with guaranteed 6-month warranty.', 7, '19, Kshirsagar Colony', 'Ujjain', 'Madhya Pradesh', '456006', 23.1895120, 75.7745120, 'ACTIVE', 'AADHAAR', '111234981131', 4.80, 15, 19, 13400.00),
(10, 31, 'High voltage transformer, industrial switchboards, and residential inverters/solar installation expert.', 12, '45, Indira Nagar, Maksi Road', 'Ujjain', 'Madhya Pradesh', '456010', 23.1712450, 75.8145120, 'ACTIVE', 'AADHAAR', '661234981132', 4.95, 26, 32, 23500.00),
(11, 32, 'Comprehensive cassette and split AC servicing, PCB circuit replacements, and seasonal overhaul.', 5, '108, Vijay Nagar, AB Road', 'Indore', 'Madhya Pradesh', '452010', 22.7533120, 75.8937120, 'ACTIVE', 'AADHAAR', '881234981133', 4.88, 25, 31, 24600.00),
(12, 33, 'Commercial plumbing contractor for high-rise societies, drainage cleaning, and pressure pump fittings.', 8, '54, Palasia, Old Palasia Main Road', 'Indore', 'Madhya Pradesh', '452001', 22.7244120, 75.8839120, 'ACTIVE', 'AADHAAR', '991234981134', 4.78, 21, 27, 16800.00),
(13, 34, 'Modular cabinet designs, bespoke wooden tables, laminate repair, and antique furniture restoration.', 9, '201, Rajendra Nagar', 'Indore', 'Madhya Pradesh', '452012', 22.6781230, 75.8312450, 'ACTIVE', 'AADHAAR', '331234981135', 4.82, 17, 21, 19400.00),
(14, 35, 'Full-house wiring renovation, LED concealed profiles, and smart home automation setups.', 7, '77, MP Nagar Zone-II', 'Bhopal', 'Madhya Pradesh', '462011', 23.2332120, 77.4343120, 'ACTIVE', 'AADHAAR', '441234981136', 4.92, 23, 29, 21200.00),
(15, 36, 'Intensive commercial office cleaning, floor scrubbing, and post-construction sanitization.', 6, '15, Arera Colony, E-Sector', 'Bhopal', 'Madhya Pradesh', '462016', 23.2114120, 77.4412340, 'ACTIVE', 'AADHAAR', '551234981137', 4.74, 18, 24, 18300.00),
(16, 37, 'RO water purifier servicing, membrane change, TDS balancing, and water softener installation.', 8, '63, Kolar Road, Near Sarvdharm', 'Bhopal', 'Madhya Pradesh', '462042', 23.1784120, 77.4196120, 'ACTIVE', 'AADHAAR', '771234981138', 4.86, 20, 25, 14900.00),
(17, 38, 'Smartphone screen replacement, battery replacement, water damage repair for iPhone and Android.', 5, '31, Station Road, Near Railway Station', 'Dewas', 'Madhya Pradesh', '455001', 22.9654120, 76.0587120, 'ACTIVE', 'AADHAAR', '221234981139', 4.79, 16, 20, 11700.00),
(18, 39, 'Residential exterior weather-shield painting, dampness sealing, and crack filling specialist.', 10, '82, Vikas Nagar', 'Dewas', 'Madhya Pradesh', '455001', 22.9712450, 76.0645120, 'ACTIVE', 'AADHAAR', '111234981140', 4.70, 13, 17, 15800.00),
(19, 40, 'Eco-friendly pest management and mosquito fogging services for bungalows and apartments.', 3, '40, Alkapuri', 'Dewas', 'Madhya Pradesh', '455001', 22.9589120, 76.0512450, 'PENDING_VERIFICATION', 'AADHAAR', '661234981141', 0.00, 0, 0, 0.00),
(20, 41, 'Desktop workstation assembly, GPU diagnosis, CCTV camera installation and NVR configuration.', 6, '59, Mahakal Commercial Complex', 'Ujjain', 'Madhya Pradesh', '456006', 23.1812450, 75.7712450, 'ACTIVE', 'AADHAAR', '881234981142', 4.87, 18, 22, 13900.00);

-- ----------------------------------------------------------------------------
-- 4. SEED CUSTOMER ADDRESSES
-- ----------------------------------------------------------------------------
INSERT INTO addresses (address_id, customer_id, label, street_address, landmark, city, state, pincode, latitude, longitude, is_default) VALUES
(1, 1, 'Home', 'Flat 302, Royal Residency, Freeganj', 'Opposite Hotel Imperial', 'Ujjain', 'Madhya Pradesh', '456001', 23.1772100, 75.7871200, TRUE),
(2, 1, 'Office', 'Shop 12, Tower Commercial Hub', 'Near Tower Chowk', 'Ujjain', 'Madhya Pradesh', '456001', 23.1695400, 75.7824100, FALSE),
(3, 2, 'Home', 'Plot 45, Mahakal Van, Chintaman Road', 'Near Mahakal Temple Bypass', 'Ujjain', 'Madhya Pradesh', '456006', 23.1811200, 75.7654100, TRUE),
(4, 3, 'Home', 'B-14, Vikramaditya Nagar, Nanakheda', 'Near Sanwer Road Bus Stand', 'Ujjain', 'Madhya Pradesh', '456010', 23.1542100, 75.7923400, TRUE),
(5, 4, 'Home', '22, Madhav Nagar Main Colony', 'Behind SBI Bank', 'Ujjain', 'Madhya Pradesh', '456001', 23.1684500, 75.7801200, TRUE),
(6, 5, 'Home', '89, Rishi Nagar, Sector C', 'Near Community Park', 'Ujjain', 'Madhya Pradesh', '456010', 23.1594100, 75.7785400, TRUE),
(7, 6, 'Home', '104, Sethi Nagar Avenue', 'Near Police Control Room', 'Ujjain', 'Madhya Pradesh', '456010', 23.1651200, 75.8021400, TRUE),
(8, 7, 'Home', '15, Agar Road, Chimanganj', 'Near Krishi Upaj Mandi Gate', 'Ujjain', 'Madhya Pradesh', '456006', 23.2021400, 75.7912400, TRUE),
(9, 8, 'Home', '31, Kshirsagar Ghat Marg', 'Near Ramghat Crossing', 'Ujjain', 'Madhya Pradesh', '456006', 23.1884100, 75.7732100, TRUE),
(10, 9, 'Home', '67, Indira Nagar Extension', 'Opposite Petrol Pump', 'Ujjain', 'Madhya Pradesh', '456010', 23.1721400, 75.8132100, TRUE),
(11, 10, 'Home', '405, Sapphire Heights, AB Road', 'Near Vijay Nagar Square', 'Indore', 'Madhya Pradesh', '452010', 22.7541200, 75.8941200, TRUE),
(12, 11, 'Home', '18, Old Palasia, Saket Road', 'Near Anand Bazaar', 'Indore', 'Madhya Pradesh', '452001', 22.7251400, 75.8841200, TRUE),
(13, 12, 'Home', '92, Rajendra Nagar, Silicon City Road', 'Near Metro Station Pillar 12', 'Indore', 'Madhya Pradesh', '452012', 22.6791200, 75.8321400, TRUE),
(14, 13, 'Home', 'B-34, Mansarovar Complex, MP Nagar', 'Near Habibganj Station', 'Bhopal', 'Madhya Pradesh', '462011', 23.2341200, 77.4351200, TRUE),
(15, 14, 'Home', 'E-4/12, Arera Colony', 'Opposite Campion School', 'Bhopal', 'Madhya Pradesh', '462016', 23.2121400, 77.4421400, TRUE),
(16, 15, 'Home', '112, Kolar Road, Sarvdharm C-Sector', 'Near D-Mart', 'Bhopal', 'Madhya Pradesh', '462042', 23.1791200, 77.4201400, TRUE),
(17, 16, 'Home', '25, Station Road, Jawahar Chowk', 'Near Dewas Gate', 'Dewas', 'Madhya Pradesh', '455001', 22.9661200, 76.0591200, TRUE),
(18, 17, 'Home', '73, Vikas Nagar, AB Road', 'Behind Chamunda Mata Mandir', 'Dewas', 'Madhya Pradesh', '455001', 22.9721400, 76.0651200, TRUE),
(19, 18, 'Home', '48, Alkapuri Colony', 'Near City Civil Hospital', 'Dewas', 'Madhya Pradesh', '455001', 22.9591200, 76.0521400, TRUE),
(20, 19, 'Home', 'Flat 101, Mahakal Heritage, Kot Mohalla', 'Near Shipra River Bank', 'Ujjain', 'Madhya Pradesh', '456006', 23.1831200, 75.7671200, TRUE),
(21, 20, 'Home', '84, Sandipani Marg, Ved Nagar', 'Near ISKCON Temple', 'Ujjain', 'Madhya Pradesh', '456010', 23.1561200, 75.7861200, TRUE);

-- ----------------------------------------------------------------------------
-- 5. SEED CATEGORIES (10 Categories)
-- ----------------------------------------------------------------------------
INSERT INTO categories (category_id, name, slug, description, icon, is_active, display_order) VALUES
(1, 'Electrical Solutions', 'electrical', 'Complete electrical installations, short circuit fixes, wiring, and appliance setups.', 'zap', TRUE, 1),
(2, 'AC & Cooling Systems', 'ac-cooling', 'Split and window AC installation, deep jet cleaning, gas charging, and PCB repair.', 'wind', TRUE, 2),
(3, 'Plumbing Services', 'plumbing', 'Tap fittings, drainage unclogging, water tank repair, geyser & sanitary installations.', 'droplet', TRUE, 3),
(4, 'Deep Home Cleaning', 'cleaning', 'Full house sanitization, kitchen chimney cleaning, bathroom disinfection, and sofa wash.', 'sparkles', TRUE, 4),
(5, 'Carpentry & Woodwork', 'carpentry', 'Custom modular kitchen fixes, wooden door lock repairs, hinge adjustments, and assembly.', 'hammer', TRUE, 5),
(6, 'Home Appliance Repair', 'appliance-repair', 'Doorstep diagnostics and repairs for washing machines, refrigerators, and microwaves.', 'settings', TRUE, 6),
(7, 'Computer & Laptop Care', 'computer-repair', 'Hardware upgrades, OS installation, motherboard chip-level repair, and data recovery.', 'monitor', TRUE, 7),
(8, 'Smartphone & Tablet Repair', 'mobile-repair', 'Original screen replacements, charging port fixes, battery swaps, and water damage recovery.', 'smartphone', TRUE, 8),
(9, 'Painting & Waterproofing', 'painting', 'Premium interior wall painting, exterior weatherproofing, damp treatment, and putty work.', 'brush', TRUE, 9),
(10, 'Pest Control Services', 'pest-control', 'Eco-friendly odorless pest termination for termites, cockroaches, bedbugs, and rodents.', 'shield-alert', TRUE, 10);

-- ----------------------------------------------------------------------------
-- 6. SEED SERVICES (30 Services)
-- ----------------------------------------------------------------------------
INSERT INTO services (service_id, category_id, service_name, slug, description, base_price, duration_minutes, is_active) VALUES
-- Electrical
(1, 1, 'Emergency Electrician Visit', 'emergency-electrician', 'Inspection and diagnosis of power tripping, burnt wire smell, and circuit failure.', 249.00, 45, TRUE),
(2, 1, 'Ceiling Fan Installation & Repair', 'fan-installation', 'Mounting, regulator testing, and bearing noise resolution for ceiling/exhaust fans.', 199.00, 30, TRUE),
(3, 1, 'MCB Box Replacement & Troubleshooting', 'mcb-replacement', 'Replacement of miniature circuit breakers and heavy load distribution balancing.', 499.00, 60, TRUE),
-- AC & Cooling
(4, 2, 'AC Jet Foam Deep Servicing', 'ac-jet-service', 'High-pressure foam jet wash for indoor cooling coils and outdoor condenser unit.', 499.00, 60, TRUE),
(5, 2, 'AC Complete Gas Charging', 'ac-gas-charging', 'Leak identification, nitrogen pressure test, and full R32/R410A refrigerant gas filling.', 1899.00, 90, TRUE),
(6, 2, 'Split AC Uninstallation & Installation', 'ac-install-uninstall', 'Standard unmounting and re-installation with wall bracket fitting and vacuum test.', 1299.00, 120, TRUE),
-- Plumbing
(7, 3, 'Tap, Mixer & Faucet Repair', 'tap-repair', 'Replacement of washers, spindle cartridges, and leaky bathroom faucet fittings.', 199.00, 30, TRUE),
(8, 3, 'Drainage & Pipe Unclogging', 'drain-unclogging', 'Mechanical drain snake clearance for choked kitchen sinks and bathroom waste lines.', 399.00, 45, TRUE),
(9, 3, 'Water Tank Mechanical Cleaning', 'tank-cleaning', 'UV-treated 5-stage cleaning and sludge evacuation for overhead tanks up to 1000L.', 799.00, 90, TRUE),
-- Cleaning
(10, 4, 'Full House Deep Cleaning (2 BHK)', 'home-cleaning-2bhk', 'Mechanized scrubbing of floors, balcony washing, cobweb removal, and cabinet wiping.', 2499.00, 240, TRUE),
(11, 4, 'Kitchen Deep Sanitization', 'kitchen-cleaning', 'Heavy degreasing of tiles, chimney exhaust hood, slab granite, and under-sink zones.', 1199.00, 120, TRUE),
(12, 4, 'Sofa & Upholstery Shampooing', 'sofa-shampooing', 'Dry vacuuming followed by shampoo foam extraction for 5-seater fabric sofas.', 799.00, 60, TRUE),
-- Carpentry
(13, 5, 'Door Lock & Latch Installation', 'door-lock-repair', 'Fitting of high-security mortise locks, cylindrical handles, and latch alignment.', 349.00, 45, TRUE),
(14, 5, 'Modular Furniture Assembly', 'furniture-assembly', 'Professional assembly for flat-pack wardrobes, study tables, and bed frames.', 699.00, 90, TRUE),
(15, 5, 'Cabinet Hinge & Channel Repair', 'cabinet-hinge-repair', 'Replacement of soft-close hydraulic hinges and drawer telescopic sliding tracks.', 299.00, 45, TRUE),
-- Appliance Repair
(16, 6, 'Washing Machine Diagnosis & Repair', 'washing-machine-repair', 'Troubleshooting spin errors, drum balancing, water inlet valves, and motor belt issues.', 399.00, 60, TRUE),
(17, 6, 'Refrigerator Cooling Problem Fix', 'refrigerator-repair', 'Thermostat inspection, compressor relay replacement, and defrost sensor repair.', 449.00, 60, TRUE),
(18, 6, 'Microwave Oven Doorstep Service', 'microwave-repair', 'Fixing magnetron heating failure, touch panel malfunction, and turn-table motor.', 399.00, 45, TRUE),
(19, 6, 'RO Water Purifier Servicing', 'ro-servicing', 'Sediment, carbon filter change, membrane flushing, and total dissolved solids (TDS) tuning.', 499.00, 60, TRUE),
-- Computer & Laptop
(20, 7, 'Laptop Thermal Paste & Overheating Fix', 'laptop-thermal-service', 'Complete internal fan de-dusting and Arctic thermal paste application on CPU/GPU.', 599.00, 60, TRUE),
(21, 7, 'SSD Upgrade & OS Cloning', 'ssd-upgrade-os', 'NVMe / SATA SSD installation with seamless OS migration without data loss.', 699.00, 90, TRUE),
(22, 7, 'Motherboard Chip-Level Diagnosis', 'motherboard-repair', 'Micro-soldering, short capacitor replacement, and power IC diagnostics.', 999.00, 120, TRUE),
-- Smartphone Repair
(23, 8, 'Doorstep Screen Replacement', 'mobile-screen-replacement', 'Original specification OLED / IPS LCD screen installation with touch calibration.', 1299.00, 60, TRUE),
(24, 8, 'Battery Health Swap & Replacement', 'mobile-battery-swap', 'High-capacity OEM battery installation with 6-month backup warranty.', 799.00, 45, TRUE),
(25, 8, 'Charging Jack & Speaker Fix', 'charging-jack-fix', 'Replacement of sub-board ribbon, type-C charging port, and ear speaker module.', 499.00, 45, TRUE),
-- Painting
(26, 9, 'Single Room Fresh Emulsion Painting', 'single-room-painting', 'Double coat Asian Paints Tractor / Apcolite emulsion on ceiling and walls up to 120 sq ft.', 2999.00, 360, TRUE),
(27, 9, 'Wall Dampness & Crack Waterproofing', 'wall-damp-treatment', 'Doctor Fixit elastomeric treatment with primer barrier against monsoon seepage.', 1499.00, 180, TRUE),
-- Pest Control
(28, 10, 'Herbal Cockroach & Ant Management', 'cockroach-pest-control', 'Odorless gel application in kitchen nooks and drain pipes with 100% human safety.', 699.00, 45, TRUE),
(29, 10, 'Anti-Termite Drilling Treatment', 'termite-treatment', 'Chemical soil barrier drilling at 1-foot intervals along skirting boards with 1-year guarantee.', 1999.00, 180, TRUE),
(30, 10, 'Bed Bug Dual-Session Eradication', 'bed-bug-eradication', 'Double intensive spray treatment covering mattresses, headboards, and wardrobes.', 1299.00, 90, TRUE);

-- ----------------------------------------------------------------------------
-- 7. SEED PROVIDER_SERVICES (50+ Custom Price Mappings)
-- ----------------------------------------------------------------------------
INSERT INTO provider_services (provider_id, service_id, custom_price, is_available) VALUES
-- Provider 1: Rajesh Sharma (Electrician, Ujjain)
(1, 1, 249.00, TRUE), (1, 2, 199.00, TRUE), (1, 3, 499.00, TRUE),
-- Provider 2: Amit Verma (AC, Ujjain)
(2, 4, 549.00, TRUE), (2, 5, 1950.00, TRUE), (2, 6, 1349.00, TRUE),
-- Provider 3: Dinesh Malviya (Plumber, Ujjain)
(3, 7, 219.00, TRUE), (3, 8, 429.00, TRUE), (3, 9, 849.00, TRUE),
-- Provider 4: Sunil Carpenter (Carpenter, Ujjain)
(4, 13, 349.00, TRUE), (4, 14, 749.00, TRUE), (4, 15, 319.00, TRUE),
-- Provider 5: Santosh Yadav (Cleaning, Ujjain)
(5, 10, 2599.00, TRUE), (5, 11, 1249.00, TRUE), (5, 12, 849.00, TRUE),
-- Provider 6: Mukesh Prajapati (Appliances, Ujjain)
(6, 16, 429.00, TRUE), (6, 17, 499.00, TRUE), (6, 18, 419.00, TRUE), (6, 19, 529.00, TRUE),
-- Provider 7: Vivek Chaurasia (Laptops, Ujjain)
(7, 20, 649.00, TRUE), (7, 21, 749.00, TRUE), (7, 22, 1099.00, TRUE),
-- Provider 8: Bhupendra Lodhi (Painting, Ujjain)
(8, 26, 3199.00, TRUE), (8, 27, 1599.00, TRUE),
-- Provider 9: Chetan Bhati (Pest Control, Ujjain)
(9, 28, 749.00, TRUE), (9, 29, 2199.00, TRUE), (9, 30, 1399.00, TRUE),
-- Provider 10: Deepak Sikarwar (Electrician, Ujjain)
(10, 1, 279.00, TRUE), (10, 2, 229.00, TRUE), (10, 3, 549.00, TRUE),
-- Provider 11: Gopal Baghel (AC, Indore)
(11, 4, 529.00, TRUE), (11, 5, 1899.00, TRUE), (11, 6, 1299.00, TRUE),
-- Provider 12: Hemant Nagar (Plumber, Indore)
(12, 7, 199.00, TRUE), (12, 8, 399.00, TRUE), (12, 9, 799.00, TRUE),
-- Provider 13: Inderjeet Singh (Carpenter, Indore)
(13, 13, 379.00, TRUE), (13, 14, 799.00, TRUE), (13, 15, 349.00, TRUE),
-- Provider 14: Jagdish Chauhan (Electrician, Bhopal)
(14, 1, 269.00, TRUE), (14, 2, 219.00, TRUE), (14, 3, 529.00, TRUE),
-- Provider 15: Kailash Meena (Cleaning, Bhopal)
(15, 10, 2699.00, TRUE), (15, 11, 1299.00, TRUE), (15, 12, 899.00, TRUE),
-- Provider 16: Laxman Dhangar (Appliances, Bhopal)
(16, 16, 449.00, TRUE), (16, 17, 499.00, TRUE), (16, 19, 549.00, TRUE),
-- Provider 17: Manoj Kushwaha (Mobiles, Dewas)
(17, 23, 1399.00, TRUE), (17, 24, 849.00, TRUE), (17, 25, 529.00, TRUE),
-- Provider 18: Nandkishore Gour (Painting, Dewas)
(18, 26, 2999.00, TRUE), (18, 27, 1499.00, TRUE),
-- Provider 20: Radheshyam Kulmi (Laptops, Ujjain)
(20, 20, 599.00, TRUE), (20, 21, 699.00, TRUE), (20, 22, 999.00, TRUE);

-- ----------------------------------------------------------------------------
-- 8. SEED SLOTS (Canonical Schedule Windows)
-- ----------------------------------------------------------------------------
INSERT INTO slots (slot_id, slot_name, start_time, end_time) VALUES
(1, 'Morning Slot A (09:00 - 11:00)', '09:00:00', '11:00:00'),
(2, 'Morning Slot B (11:00 - 13:00)', '11:00:00', '13:00:00'),
(3, 'Afternoon Slot A (14:00 - 16:00)', '14:00:00', '16:00:00'),
(4, 'Afternoon Slot B (16:00 - 18:00)', '16:00:00', '18:00:00'),
(5, 'Evening Slot (18:00 - 20:00)', '18:00:00', '20:00:00');

-- ----------------------------------------------------------------------------
-- 9. SEED PROVIDER_SLOTS (Providers active Mon-Sat across slots)
-- ----------------------------------------------------------------------------
INSERT INTO provider_slots (provider_id, day_of_week, slot_id, is_active)
SELECT p.provider_id, d.day_val, s.slot_id, TRUE
FROM service_providers p
CROSS JOIN (SELECT 1 AS day_val UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) d
CROSS JOIN slots s
WHERE p.provider_id <= 20;

-- ----------------------------------------------------------------------------
-- 10. SEED BOOKINGS (100+ Realistic Multi-Status Transactions)
-- Formula:
-- base_amount = provider custom price
-- platform_fee = 10% of base_amount
-- tax_amount = 18% of platform_fee
-- total_amount = base_amount + platform_fee + tax_amount
-- provider_earnings = base_amount (or 90% payout)
-- ----------------------------------------------------------------------------

-- Group 1: Completed Bookings (Past dates, fully paid, provider wallet credited)
INSERT INTO bookings 
(booking_id, booking_number, customer_id, provider_id, service_id, address_id, booking_date, slot_id, problem_description, base_amount, platform_fee, tax_amount, discount_amount, total_amount, provider_earnings, booking_status, payment_status, created_at)
VALUES
(1, 'FM-2026-0001', 1, 1, 1, 1, '2026-08-01', 1, 'Main hall MCB keeps tripping whenever geyser is turned on.', 249.00, 24.90, 4.48, 0.00, 278.38, 249.00, 'COMPLETED', 'PAID', '2026-07-31 10:15:00'),
(2, 'FM-2026-0002', 2, 2, 4, 3, '2026-08-02', 2, 'Split AC cooling drastically reduced, water dripping from indoor blower.', 549.00, 54.90, 9.88, 0.00, 613.78, 549.00, 'COMPLETED', 'PAID', '2026-08-01 14:20:00'),
(3, 'FM-2026-0003', 3, 3, 7, 4, '2026-08-03', 3, 'Kitchen mixer tap continuously leaking from swivel spout base.', 219.00, 21.90, 3.94, 0.00, 244.84, 219.00, 'COMPLETED', 'PAID', '2026-08-02 09:30:00'),
(4, 'FM-2026-0004', 4, 4, 13, 5, '2026-08-04', 4, 'Main entrance Godrej lock cylinder jammed, key stuck halfway.', 349.00, 34.90, 6.28, 0.00, 390.18, 349.00, 'COMPLETED', 'PAID', '2026-08-03 11:45:00'),
(5, 'FM-2026-0005', 5, 5, 11, 6, '2026-08-05', 1, 'Exhaust chimney completely choked with grease, motor humming.', 1249.00, 124.90, 22.48, 0.00, 1396.38, 1249.00, 'COMPLETED', 'PAID', '2026-08-04 16:10:00'),
(6, 'FM-2026-0006', 6, 6, 16, 7, '2026-08-06', 2, 'IFB front load washing machine displaying error code dE and not spinning.', 429.00, 42.90, 7.72, 0.00, 479.62, 429.00, 'COMPLETED', 'PAID', '2026-08-05 18:00:00'),
(7, 'FM-2026-0007', 7, 7, 20, 8, '2026-08-07', 3, 'Dell Inspiron heating up within 10 mins and shutting down unexpectedly.', 649.00, 64.90, 11.68, 0.00, 725.58, 649.00, 'COMPLETED', 'PAID', '2026-08-06 12:20:00'),
(8, 'FM-2026-0008', 8, 8, 27, 9, '2026-08-08', 4, 'North-facing bedroom wall paint peeling off due to balcony seepage.', 1599.00, 159.90, 28.78, 0.00, 1787.68, 1599.00, 'COMPLETED', 'PAID', '2026-08-07 15:30:00'),
(9, 'FM-2026-0009', 9, 9, 28, 10, '2026-08-09', 1, 'Heavy cockroach infestation inside kitchen lower drawers.', 749.00, 74.90, 13.48, 0.00, 837.38, 749.00, 'COMPLETED', 'PAID', '2026-08-08 08:45:00'),
(10, 'FM-2026-0010', 10, 11, 4, 11, '2026-08-10', 2, 'Seasonal AC service before family function.', 529.00, 52.90, 9.52, 0.00, 591.42, 529.00, 'COMPLETED', 'PAID', '2026-08-09 11:15:00'),
(11, 'FM-2026-0011', 11, 12, 8, 12, '2026-08-11', 3, 'Balcony rainwater outlet pipe choked with dry leaves and mud.', 399.00, 39.90, 7.18, 0.00, 446.08, 399.00, 'COMPLETED', 'PAID', '2026-08-10 13:40:00'),
(12, 'FM-2026-0012', 12, 13, 14, 13, '2026-08-12', 4, 'Assembly of newly delivered 6-door engineered wood wardrobe.', 799.00, 79.90, 14.38, 0.00, 893.28, 799.00, 'COMPLETED', 'PAID', '2026-08-11 17:00:00'),
(13, 'FM-2026-0013', 13, 14, 2, 14, '2026-08-13', 1, 'Crompton ceiling fan making grinding metallic sound on speed 4.', 219.00, 21.90, 3.94, 0.00, 244.84, 219.00, 'COMPLETED', 'PAID', '2026-08-12 10:10:00'),
(14, 'FM-2026-0014', 14, 15, 12, 15, '2026-08-14', 2, 'L-shape velvet sofa deep shampooing and dust-mite cleaning.', 899.00, 89.90, 16.18, 0.00, 1005.08, 899.00, 'COMPLETED', 'PAID', '2026-08-13 14:50:00'),
(15, 'FM-2026-0015', 15, 16, 17, 16, '2026-08-15', 3, 'Whirlpool double door fridge freezer cooling but lower food cabinet warm.', 499.00, 49.90, 8.98, 0.00, 557.88, 499.00, 'COMPLETED', 'PAID', '2026-08-14 12:00:00'),
(16, 'FM-2026-0016', 16, 17, 23, 17, '2026-08-16', 4, 'OnePlus Nord broken screen replacement after fall on concrete floor.', 1399.00, 139.90, 25.18, 0.00, 1564.08, 1399.00, 'COMPLETED', 'PAID', '2026-08-15 16:30:00'),
(17, 'FM-2026-0017', 17, 18, 26, 18, '2026-08-17', 1, 'Drawing room accent wall royal luster painting.', 2999.00, 299.90, 53.98, 0.00, 3352.88, 2999.00, 'COMPLETED', 'PAID', '2026-08-16 09:15:00'),
(18, 'FM-2026-0018', 18, 10, 1, 19, '2026-08-18', 2, 'Emergency fuse box burning smell during storm.', 279.00, 27.90, 5.02, 0.00, 311.92, 279.00, 'COMPLETED', 'PAID', '2026-08-17 19:40:00'),
(19, 'FM-2026-0019', 19, 20, 21, 20, '2026-08-19', 3, 'Crucial 1TB NVMe SSD installation and Windows 11 cloning.', 699.00, 69.90, 12.58, 0.00, 781.48, 699.00, 'COMPLETED', 'PAID', '2026-08-18 11:20:00'),
(20, 'FM-2026-0020', 20, 2, 5, 21, '2026-08-20', 4, 'Carrier 1.5 Ton AC gas leakage repair and full recharge.', 1950.00, 195.00, 35.10, 0.00, 2180.10, 1950.00, 'COMPLETED', 'PAID', '2026-08-19 15:45:00');

-- Insert more historical completed bookings (21 to 70) for analytics richness
INSERT INTO bookings 
(booking_id, booking_number, customer_id, provider_id, service_id, address_id, booking_date, slot_id, problem_description, base_amount, platform_fee, tax_amount, discount_amount, total_amount, provider_earnings, booking_status, payment_status, created_at)
SELECT 
    20 + n,
    CONCAT('FM-2026-00', 20 + n),
    ((n % 20) + 1),
    ((n % 18) + 1),
    ((n % 30) + 1),
    ((n % 21) + 1),
    DATE_SUB('2026-08-25', INTERVAL (n % 24) DAY),
    ((n % 5) + 1),
    CONCAT('Routine professional maintenance service request #', n),
    500.00 + (n * 15),
    50.00 + (n * 1.5),
    9.00 + (n * 0.27),
    0.00,
    559.00 + (n * 16.77),
    500.00 + (n * 15),
    'COMPLETED',
    'PAID',
    DATE_SUB('2026-08-24 10:00:00', INTERVAL (n % 24) DAY)
FROM (
    SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 UNION
    SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION
    SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30 UNION
    SELECT 31 UNION SELECT 32 UNION SELECT 33 UNION SELECT 34 UNION SELECT 35 UNION
    SELECT 36 UNION SELECT 37 UNION SELECT 38 UNION SELECT 39 UNION SELECT 40 UNION
    SELECT 41 UNION SELECT 42 UNION SELECT 43 UNION SELECT 44 UNION SELECT 45 UNION
    SELECT 46 UNION SELECT 47 UNION SELECT 48 UNION SELECT 49 UNION SELECT 50
) num_seq;

-- Group 2: Active / In-Progress / On The Way / Accepted Bookings (Today & Tomorrow)
INSERT INTO bookings 
(booking_id, booking_number, customer_id, provider_id, service_id, address_id, booking_date, slot_id, problem_description, base_amount, platform_fee, tax_amount, discount_amount, total_amount, provider_earnings, booking_status, payment_status, created_at)
VALUES
(71, 'FM-2026-0071', 1, 1, 1, 1, '2026-08-30', 2, 'Main breaker keeps tripping during inverter charging.', 249.00, 24.90, 4.48, 0.00, 278.38, 249.00, 'IN_PROGRESS', 'PAID', '2026-08-30 08:30:00'),
(72, 'FM-2026-0072', 2, 2, 4, 3, '2026-08-30', 3, 'AC jet foam cleaning needed urgently before evening party.', 549.00, 54.90, 9.88, 0.00, 613.78, 549.00, 'ON_THE_WAY', 'PAID', '2026-08-30 09:10:00'),
(73, 'FM-2026-0073', 3, 3, 7, 4, '2026-08-30', 4, 'Bathroom angle valve broke, water gushing out.', 219.00, 21.90, 3.94, 0.00, 244.84, 219.00, 'ACCEPTED', 'PAID', '2026-08-30 10:00:00'),
(74, 'FM-2026-0074', 4, 6, 16, 5, '2026-08-31', 1, 'Samsung top-load washing machine making high vibration noise.', 429.00, 42.90, 7.72, 0.00, 479.62, 429.00, 'ACCEPTED', 'PAID', '2026-08-30 10:45:00'),
(75, 'FM-2026-0075', 5, 7, 20, 6, '2026-08-31', 2, 'Laptop fan making rattling noise and CPU throttling.', 649.00, 64.90, 11.68, 0.00, 725.58, 649.00, 'PENDING', 'PAID', '2026-08-30 11:15:00'),
(76, 'FM-2026-0076', 6, 9, 28, 7, '2026-08-31', 3, 'Pest control service required for modular kitchen.', 749.00, 74.90, 13.48, 0.00, 837.38, 749.00, 'PENDING', 'PENDING', '2026-08-30 11:20:00');

-- Group 3: Cancelled & Rejected Bookings (with reasons for analytics)
INSERT INTO bookings 
(booking_id, booking_number, customer_id, provider_id, service_id, address_id, booking_date, slot_id, problem_description, base_amount, platform_fee, tax_amount, discount_amount, total_amount, provider_earnings, booking_status, payment_status, cancellation_reason, cancelled_by, created_at)
VALUES
(77, 'FM-2026-0077', 7, 1, 3, 8, '2026-08-20', 1, 'Need MCB upgrade for new 2-ton AC.', 499.00, 49.90, 8.98, 0.00, 557.88, 499.00, 'CANCELLED', 'REFUNDED', 'Customer had an emergency travel out of town.', 'CUSTOMER', '2026-08-19 14:00:00'),
(78, 'FM-2026-0078', 8, 2, 6, 9, '2026-08-21', 2, 'Split AC shifted to another room.', 1349.00, 134.90, 24.28, 0.00, 1508.18, 1349.00, 'REJECTED', 'REFUNDED', 'Provider unavailable due to illness.', 'PROVIDER', '2026-08-20 16:30:00'),
(79, 'FM-2026-0079', 9, 4, 14, 10, '2026-08-22', 3, 'Wardrobe assembly.', 749.00, 74.90, 13.48, 0.00, 837.38, 749.00, 'CANCELLED', 'REFUNDED', 'Delivery was delayed by courier.', 'CUSTOMER', '2026-08-21 11:20:00'),
(80, 'FM-2026-0080', 10, 5, 10, 11, '2026-08-23', 4, 'Deep cleaning of flat before shifting.', 2599.00, 259.90, 46.78, 0.00, 2905.68, 2599.00, 'CANCELLED', 'REFUNDED', 'Renovation work incomplete.', 'CUSTOMER', '2026-08-22 09:10:00');

-- Group 4: Fill remaining bookings up to 105 total records
INSERT INTO bookings 
(booking_id, booking_number, customer_id, provider_id, service_id, address_id, booking_date, slot_id, problem_description, base_amount, platform_fee, tax_amount, discount_amount, total_amount, provider_earnings, booking_status, payment_status, created_at)
SELECT 
    80 + m,
    CONCAT('FM-2026-00', 80 + m),
    ((m % 20) + 1),
    ((m % 18) + 1),
    ((m % 28) + 1),
    ((m % 21) + 1),
    DATE_SUB('2026-08-29', INTERVAL (m % 20) DAY),
    ((m % 5) + 1),
    CONCAT('Comprehensive doorstep technical checkup #', m),
    450.00 + (m * 20),
    45.00 + (m * 2.0),
    8.10 + (m * 0.36),
    0.00,
    503.10 + (m * 22.36),
    450.00 + (m * 20),
    'COMPLETED',
    'PAID',
    DATE_SUB('2026-08-28 12:00:00', INTERVAL (m % 20) DAY)
FROM (
    SELECT 1 AS m UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 UNION
    SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25
) rem_seq;

-- ----------------------------------------------------------------------------
-- 11. SEED BOOKING_STATUS_HISTORY
-- ----------------------------------------------------------------------------
INSERT INTO booking_status_history (booking_id, previous_status, new_status, changed_by_user_id, remarks, changed_at)
VALUES
(1, NULL, 'PENDING', 2, 'Booking submitted by customer', '2026-07-31 10:15:00'),
(1, 'PENDING', 'ACCEPTED', 22, 'Rajesh Sharma confirmed the booking', '2026-07-31 11:00:00'),
(1, 'ACCEPTED', 'ON_THE_WAY', 22, 'Technician departed for location', '2026-08-01 08:35:00'),
(1, 'ON_THE_WAY', 'IN_PROGRESS', 22, 'Started circuit diagnostics', '2026-08-01 09:05:00'),
(1, 'IN_PROGRESS', 'COMPLETED', 22, 'Fixed short circuit and replaced faulty 16A MCB', '2026-08-01 10:15:00'),
(2, NULL, 'PENDING', 3, 'Booking submitted', '2026-08-01 14:20:00'),
(2, 'PENDING', 'ACCEPTED', 23, 'Amit Verma accepted the appointment', '2026-08-01 15:00:00'),
(2, 'ACCEPTED', 'COMPLETED', 23, 'AC deep chemical jet cleaning concluded', '2026-08-02 12:45:00'),
(71, NULL, 'PENDING', 2, 'Customer created urgent request', '2026-08-30 08:30:00'),
(71, 'PENDING', 'ACCEPTED', 22, 'Technician accepted job', '2026-08-30 08:40:00'),
(71, 'ACCEPTED', 'IN_PROGRESS', 22, 'Diagnostics underway', '2026-08-30 09:00:00'),
(77, 'PENDING', 'CANCELLED', 8, 'Customer cancelled due to travel', '2026-08-19 14:00:00');

-- ----------------------------------------------------------------------------
-- 12. SEED PAYMENTS
-- ----------------------------------------------------------------------------
INSERT INTO payments 
(payment_id, booking_id, transaction_reference, payment_method, payment_gateway, gateway_order_id, gateway_payment_id, amount, status, paid_at, created_at)
VALUES
(1, 1, 'TXN-FM-2026-0801-001', 'UPI', 'MOCK', 'order_mock_001', 'pay_mock_001', 278.38, 'SUCCESS', '2026-07-31 10:16:00', '2026-07-31 10:15:00'),
(2, 2, 'TXN-FM-2026-0802-002', 'CARD', 'MOCK', 'order_mock_002', 'pay_mock_002', 613.78, 'SUCCESS', '2026-08-01 14:21:00', '2026-08-01 14:20:00'),
(3, 3, 'TXN-FM-2026-0803-003', 'UPI', 'MOCK', 'order_mock_003', 'pay_mock_003', 244.84, 'SUCCESS', '2026-08-02 09:31:00', '2026-08-02 09:30:00'),
(4, 4, 'TXN-FM-2026-0804-004', 'NETBANKING', 'MOCK', 'order_mock_004', 'pay_mock_004', 390.18, 'SUCCESS', '2026-08-03 11:46:00', '2026-08-03 11:45:00'),
(5, 5, 'TXN-FM-2026-0805-005', 'UPI', 'MOCK', 'order_mock_005', 'pay_mock_005', 1396.38, 'SUCCESS', '2026-08-04 16:11:00', '2026-08-04 16:10:00'),
(6, 6, 'TXN-FM-2026-0806-006', 'CARD', 'MOCK', 'order_mock_006', 'pay_mock_006', 479.62, 'SUCCESS', '2026-08-05 18:01:00', '2026-08-05 18:00:00'),
(7, 7, 'TXN-FM-2026-0807-007', 'UPI', 'MOCK', 'order_mock_007', 'pay_mock_007', 725.58, 'SUCCESS', '2026-08-06 12:21:00', '2026-08-06 12:20:00'),
(8, 8, 'TXN-FM-2026-0808-008', 'UPI', 'MOCK', 'order_mock_008', 'pay_mock_008', 1787.68, 'SUCCESS', '2026-08-07 15:31:00', '2026-08-07 15:30:00'),
(9, 9, 'TXN-FM-2026-0809-009', 'CARD', 'MOCK', 'order_mock_009', 'pay_mock_009', 837.38, 'SUCCESS', '2026-08-08 08:46:00', '2026-08-08 08:45:00'),
(10, 10, 'TXN-FM-2026-0810-010', 'UPI', 'MOCK', 'order_mock_010', 'pay_mock_010', 591.42, 'SUCCESS', '2026-08-09 11:16:00', '2026-08-09 11:15:00'),
(71, 71, 'TXN-FM-2026-0830-071', 'UPI', 'MOCK', 'order_mock_071', 'pay_mock_071', 278.38, 'SUCCESS', '2026-08-30 08:31:00', '2026-08-30 08:30:00'),
(77, 77, 'TXN-FM-2026-0819-077', 'UPI', 'MOCK', 'order_mock_077', 'pay_mock_077', 557.88, 'REFUNDED', '2026-08-19 14:01:00', '2026-08-19 14:00:00');

-- ----------------------------------------------------------------------------
-- 13. SEED REFUNDS
-- ----------------------------------------------------------------------------
INSERT INTO refunds (refund_id, booking_id, payment_id, amount, reason, status, gateway_refund_id, processed_at) VALUES
(1, 77, 77, 557.88, 'Booking cancelled by customer before technician dispatch', 'PROCESSED', 'rfnd_mock_001', '2026-08-19 14:05:00');

-- ----------------------------------------------------------------------------
-- 14. SEED REVIEWS (Verified Reviews for Completed Bookings)
-- ----------------------------------------------------------------------------
INSERT INTO reviews (review_id, booking_id, customer_id, provider_id, rating, comment, created_at) VALUES
(1, 1, 1, 1, 5, 'Rajesh ji arrived in 15 minutes! Quickly traced the short circuit behind the distribution box and resolved it with genuine Havells breaker. Highly recommended.', '2026-08-01 11:30:00'),
(2, 2, 2, 2, 5, 'Amit did a brilliant jet cleaning for my Daikin inverter AC. Cooling is back like brand new. Very clean work without spilling water on walls.', '2026-08-02 13:15:00'),
(3, 3, 3, 3, 5, 'Quick faucet washer change, solved water dripping issue within 20 mins. Reasonable charges and courteous behavior.', '2026-08-03 10:45:00'),
(4, 4, 4, 4, 5, 'Sunil is an expert carpenter. Fixed my jammed main door lock effortlessly. Smooth mechanism now.', '2026-08-04 12:30:00'),
(5, 5, 5, 5, 4, 'Kitchen chimney was completely cleared of oil sludge. Good professional machines used. Took slightly longer than expected but thorough.', '2026-08-05 17:00:00'),
(6, 6, 6, 6, 5, 'Identified washing machine drain pump issue immediately. Saved me thousands compared to company service quotation.', '2026-08-06 19:15:00'),
(7, 7, 7, 7, 5, 'Cleaned laptop internal fans and replaced thermal compound. Temperatures dropped from 92C to 58C. Excellent!', '2026-08-07 13:40:00'),
(8, 8, 8, 8, 4, 'Very good damp proofing coating done on bedroom walls. Waiting to see performance in rains, so far very neat finish.', '2026-08-08 16:20:00'),
(9, 9, 9, 9, 5, 'Pest treatment worked like a charm. Not a single cockroach seen after 48 hours. Odorless gel was safe for my pet.', '2026-08-09 09:30:00'),
(10, 10, 10, 11, 5, 'Top class AC maintenance in Vijay Nagar Indore. Very punctual and polite technician.', '2026-08-10 12:00:00');

-- ----------------------------------------------------------------------------
-- 15. SEED COMPLAINTS
-- ----------------------------------------------------------------------------
INSERT INTO complaints (complaint_id, complaint_number, booking_id, customer_id, subject, description, status, admin_remarks, created_at, resolved_at) VALUES
(1, 'CMP-2026-0001', 5, 5, 'Minor oil stain on wall near chimney', 'Technician left a minor splash of grease on the nearby tile during cleaning.', 'RESOLVED', 'Provider sent back to clean the tile without extra charge. Customer satisfied.', '2026-08-05 18:30:00', '2026-08-06 10:00:00'),
(2, 'CMP-2026-0002', 78, 8, 'Delayed rejection communication', 'Provider rejected booking after 2 hours causing scheduling inconvenience.', 'IN_REVIEW', 'Support contacting customer to provide complimentary discount coupon.', '2026-08-20 17:00:00', NULL),
(3, 'CMP-2026-0003', 71, 1, 'Inquiry regarding additional wiring cost', 'Seeking clarification on bill estimation for external conduit pipe.', 'OPEN', NULL, '2026-08-30 09:30:00', NULL);

-- ----------------------------------------------------------------------------
-- 16. SEED NOTIFICATIONS
-- ----------------------------------------------------------------------------
INSERT INTO notifications (notification_id, user_id, title, message, type, is_read, reference_id, created_at) VALUES
(1, 2, 'Booking Confirmed!', 'Your booking FM-2026-0071 with Rajesh Sharma is confirmed for today.', 'BOOKING_UPDATE', FALSE, 71, '2026-08-30 08:40:00'),
(2, 22, 'New Booking Received', 'You have an active job FM-2026-0071 at Freeganj, Ujjain.', 'BOOKING_UPDATE', TRUE, 71, '2026-08-30 08:30:00'),
(3, 2, 'Technician In Progress', 'Rajesh Sharma has started the service diagnosis.', 'BOOKING_UPDATE', FALSE, 71, '2026-08-30 09:00:00'),
(4, 8, 'Refund Processed', 'Refund of Rs. 557.88 for booking FM-2026-0077 has been credited.', 'PAYMENT', TRUE, 77, '2026-08-19 14:05:00'),
(5, 1, 'System Health OK', 'Database backup and slot index maintenance successfully completed.', 'SYSTEM', FALSE, NULL, '2026-08-30 06:00:00');

-- ----------------------------------------------------------------------------
-- 17. SEED PROVIDER_WALLET_LEDGER
-- ----------------------------------------------------------------------------
INSERT INTO provider_wallet_ledger (ledger_id, provider_id, booking_id, transaction_type, amount, running_balance, description, created_at) VALUES
(1, 1, 1, 'CREDIT_BOOKING_PAYOUT', 249.00, 249.00, 'Earnings credited for completed booking FM-2026-0001', '2026-08-01 10:15:00'),
(2, 2, 2, 'CREDIT_BOOKING_PAYOUT', 549.00, 549.00, 'Earnings credited for completed booking FM-2026-0002', '2026-08-02 12:45:00'),
(3, 3, 3, 'CREDIT_BOOKING_PAYOUT', 219.00, 219.00, 'Earnings credited for completed booking FM-2026-0003', '2026-08-03 10:20:00'),
(4, 4, 4, 'CREDIT_BOOKING_PAYOUT', 349.00, 349.00, 'Earnings credited for completed booking FM-2026-0004', '2026-08-04 12:10:00'),
(5, 5, 5, 'CREDIT_BOOKING_PAYOUT', 1249.00, 1249.00, 'Earnings credited for completed booking FM-2026-0005', '2026-08-05 16:30:00'),
(6, 1, NULL, 'DEBIT_WITHDRAWAL', 200.00, 49.00, 'Weekly bank payout transfer to HDFC A/C ending 4120', '2026-08-07 10:00:00'),
(7, 1, 18, 'CREDIT_BOOKING_PAYOUT', 279.00, 328.00, 'Earnings credited for completed booking FM-2026-0018', '2026-08-18 20:30:00');
