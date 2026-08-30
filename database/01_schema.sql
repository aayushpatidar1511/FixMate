-- ============================================================================
-- FixMate - Local Service Booking & Management Platform
-- File: 01_schema.sql
-- Description: Complete Relational DDL for MySQL 8.0+
-- ============================================================================

CREATE DATABASE IF NOT EXISTS fixmate_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE fixmate_db;

-- ----------------------------------------------------------------------------
-- Drop existing tables in reverse dependency order
-- ----------------------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS provider_wallet_ledger;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS complaints;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS refunds;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS booking_status_history;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS provider_slots;
DROP TABLE IF EXISTS slots;
DROP TABLE IF EXISTS provider_services;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS service_providers;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------------------------
-- 1. USERS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'PROVIDER', 'ADMIN') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'BLOCKED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_role (role),
    INDEX idx_users_status (status)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 2. CUSTOMERS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE customers (
    customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    profile_image VARCHAR(255) NULL,
    total_bookings INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customers_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 3. SERVICE PROVIDERS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE service_providers (
    provider_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    bio TEXT NULL,
    experience_years INT NOT NULL DEFAULT 1,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    verification_status ENUM('PENDING_VERIFICATION', 'ACTIVE', 'REJECTED', 'BLOCKED', 'INACTIVE') 
        NOT NULL DEFAULT 'PENDING_VERIFICATION',
    id_proof_type VARCHAR(50) NOT NULL DEFAULT 'AADHAAR',
    id_proof_number VARCHAR(100) NOT NULL,
    rating_avg DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
    rating_count INT NOT NULL DEFAULT 0,
    total_completed_jobs INT NOT NULL DEFAULT 0,
    wallet_balance DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_providers_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_providers_city (city),
    INDEX idx_providers_verification (verification_status),
    INDEX idx_providers_rating (rating_avg),
    INDEX idx_providers_lat_long (latitude, longitude)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 4. ADDRESSES TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE addresses (
    address_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    label VARCHAR(50) NOT NULL DEFAULT 'Home', -- 'Home', 'Office', 'Other'
    street_address VARCHAR(255) NOT NULL,
    landmark VARCHAR(100) NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    latitude DECIMAL(10, 7) NULL,
    longitude DECIMAL(10, 7) NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_addresses_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(customer_id) ON DELETE CASCADE,
    INDEX idx_addresses_customer (customer_id),
    INDEX idx_addresses_city (city)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 5. CATEGORIES TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE categories (
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NULL,
    icon VARCHAR(50) NOT NULL DEFAULT 'wrench',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_categories_active (is_active)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 6. SERVICES TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE services (
    service_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    service_name VARCHAR(150) NOT NULL,
    slug VARCHAR(150) NOT NULL UNIQUE,
    description TEXT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    duration_minutes INT NOT NULL DEFAULT 60,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_services_category FOREIGN KEY (category_id) 
        REFERENCES categories(category_id) ON DELETE RESTRICT,
    INDEX idx_services_category (category_id),
    INDEX idx_services_active (is_active)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 7. PROVIDER_SERVICES TABLE (Many-to-Many with Custom Price)
-- ----------------------------------------------------------------------------
CREATE TABLE provider_services (
    provider_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    custom_price DECIMAL(10, 2) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (provider_id, service_id),
    CONSTRAINT fk_ps_provider FOREIGN KEY (provider_id) 
        REFERENCES service_providers(provider_id) ON DELETE CASCADE,
    CONSTRAINT fk_ps_service FOREIGN KEY (service_id) 
        REFERENCES services(service_id) ON DELETE CASCADE,
    INDEX idx_ps_service (service_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 8. SLOTS TABLE (Canonical Daily Time Blocks)
-- ----------------------------------------------------------------------------
CREATE TABLE slots (
    slot_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    slot_name VARCHAR(50) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CONSTRAINT chk_slot_time CHECK (start_time < end_time)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 9. PROVIDER_SLOTS TABLE (Weekly Recurrence Template)
-- ----------------------------------------------------------------------------
CREATE TABLE provider_slots (
    provider_slot_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider_id BIGINT NOT NULL,
    day_of_week TINYINT NOT NULL COMMENT '1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri, 6=Sat, 7=Sun',
    slot_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_pslots_provider FOREIGN KEY (provider_id) 
        REFERENCES service_providers(provider_id) ON DELETE CASCADE,
    CONSTRAINT fk_pslots_slot FOREIGN KEY (slot_id) 
        REFERENCES slots(slot_id) ON DELETE CASCADE,
    CONSTRAINT uq_provider_day_slot UNIQUE (provider_id, day_of_week, slot_id),
    CONSTRAINT chk_day_of_week CHECK (day_of_week BETWEEN 1 AND 7)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 10. BOOKINGS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE bookings (
    booking_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_number VARCHAR(30) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    address_id BIGINT NOT NULL,
    booking_date DATE NOT NULL,
    slot_id BIGINT NOT NULL,
    problem_description TEXT NOT NULL,
    base_amount DECIMAL(10, 2) NOT NULL,
    platform_fee DECIMAL(10, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL,
    provider_earnings DECIMAL(10, 2) NOT NULL,
    booking_status ENUM('PENDING', 'ACCEPTED', 'ON_THE_WAY', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'REJECTED') 
        NOT NULL DEFAULT 'PENDING',
    payment_status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') 
        NOT NULL DEFAULT 'PENDING',
    cancellation_reason VARCHAR(255) NULL,
    cancelled_by ENUM('CUSTOMER', 'PROVIDER', 'ADMIN') NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(customer_id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_provider FOREIGN KEY (provider_id) 
        REFERENCES service_providers(provider_id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_service FOREIGN KEY (service_id) 
        REFERENCES services(service_id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_address FOREIGN KEY (address_id) 
        REFERENCES addresses(address_id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_slot FOREIGN KEY (slot_id) 
        REFERENCES slots(slot_id) ON DELETE RESTRICT,
    INDEX idx_bookings_customer (customer_id),
    INDEX idx_bookings_provider (provider_id),
    INDEX idx_bookings_status (booking_status),
    INDEX idx_bookings_date (booking_date),
    INDEX idx_bookings_provider_date_slot (provider_id, booking_date, slot_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 11. BOOKING_STATUS_HISTORY TABLE (Full Audit Trail)
-- ----------------------------------------------------------------------------
CREATE TABLE booking_status_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    previous_status VARCHAR(30) NULL,
    new_status VARCHAR(30) NOT NULL,
    changed_by_user_id BIGINT NULL,
    remarks VARCHAR(255) NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bsh_booking FOREIGN KEY (booking_id) 
        REFERENCES bookings(booking_id) ON DELETE CASCADE,
    CONSTRAINT fk_bsh_user FOREIGN KEY (changed_by_user_id) 
        REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_bsh_booking (booking_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 12. PAYMENTS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    transaction_reference VARCHAR(100) NOT NULL UNIQUE,
    payment_method ENUM('UPI', 'CARD', 'NETBANKING', 'CASH') NOT NULL,
    payment_gateway ENUM('MOCK', 'RAZORPAY') NOT NULL DEFAULT 'MOCK',
    gateway_order_id VARCHAR(100) NULL,
    gateway_payment_id VARCHAR(100) NULL,
    gateway_signature VARCHAR(255) NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_booking FOREIGN KEY (booking_id) 
        REFERENCES bookings(booking_id) ON DELETE RESTRICT,
    INDEX idx_payments_booking (booking_id),
    INDEX idx_payments_reference (transaction_reference),
    INDEX idx_payments_status (status)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 13. REFUNDS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE refunds (
    refund_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    payment_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status ENUM('NOT_REQUIRED', 'PENDING', 'PROCESSED', 'FAILED') NOT NULL DEFAULT 'PENDING',
    gateway_refund_id VARCHAR(100) NULL,
    processed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refunds_booking FOREIGN KEY (booking_id) 
        REFERENCES bookings(booking_id) ON DELETE RESTRICT,
    CONSTRAINT fk_refunds_payment FOREIGN KEY (payment_id) 
        REFERENCES payments(payment_id) ON DELETE RESTRICT,
    INDEX idx_refunds_booking (booking_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 14. REVIEWS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE reviews (
    review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    rating TINYINT NOT NULL,
    comment TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_booking FOREIGN KEY (booking_id) 
        REFERENCES bookings(booking_id) ON DELETE RESTRICT,
    CONSTRAINT fk_reviews_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(customer_id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_provider FOREIGN KEY (provider_id) 
        REFERENCES service_providers(provider_id) ON DELETE CASCADE,
    CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5),
    INDEX idx_reviews_provider (provider_id),
    INDEX idx_reviews_customer (customer_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 15. COMPLAINTS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE complaints (
    complaint_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_number VARCHAR(30) NOT NULL UNIQUE,
    booking_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    subject VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    status ENUM('OPEN', 'IN_REVIEW', 'RESOLVED', 'REJECTED') NOT NULL DEFAULT 'OPEN',
    admin_remarks TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    CONSTRAINT fk_complaints_booking FOREIGN KEY (booking_id) 
        REFERENCES bookings(booking_id) ON DELETE RESTRICT,
    CONSTRAINT fk_complaints_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(customer_id) ON DELETE CASCADE,
    INDEX idx_complaints_status (status),
    INDEX idx_complaints_customer (customer_id),
    INDEX idx_complaints_booking (booking_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 16. NOTIFICATIONS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL, -- 'BOOKING_UPDATE', 'PAYMENT', 'DISPUTE', 'SYSTEM'
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    reference_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_notifications_user_read (user_id, is_read)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 17. PROVIDER_WALLET_LEDGER TABLE (Double-Entry Financial Accounting)
-- ----------------------------------------------------------------------------
CREATE TABLE provider_wallet_ledger (
    ledger_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider_id BIGINT NOT NULL,
    booking_id BIGINT NULL,
    transaction_type ENUM('CREDIT_BOOKING_PAYOUT', 'DEBIT_COMMISSION', 'DEBIT_WITHDRAWAL', 'CREDIT_ADJUSTMENT') NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    running_balance DECIMAL(12, 2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ledger_provider FOREIGN KEY (provider_id) 
        REFERENCES service_providers(provider_id) ON DELETE CASCADE,
    CONSTRAINT fk_ledger_booking FOREIGN KEY (booking_id) 
        REFERENCES bookings(booking_id) ON DELETE SET NULL,
    INDEX idx_ledger_provider (provider_id),
    INDEX idx_ledger_created (created_at)
) ENGINE=InnoDB;
