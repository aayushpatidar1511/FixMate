-- ============================================================================
-- FixMate - Local Service Booking & Management Platform
-- File: 07_triggers.sql
-- Description: Integrity Enforcement & Automated Audit Triggers
-- ============================================================================

USE fixmate_db;

DELIMITER $$

-- ----------------------------------------------------------------------------
-- 1. TRIGGER: trg_after_review_insert
-- Purpose: Automatically recomputes and stores provider rating_avg and
-- rating_count in service_providers table after each newly submitted review.
-- ----------------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_after_review_insert$$

CREATE TRIGGER trg_after_review_insert
AFTER INSERT ON reviews
FOR EACH ROW
BEGIN
    DECLARE v_avg_rating DECIMAL(3, 2);
    DECLARE v_count INT;

    SELECT ROUND(AVG(rating), 2), COUNT(*)
    INTO v_avg_rating, v_count
    FROM reviews
    WHERE provider_id = NEW.provider_id;

    UPDATE service_providers
    SET rating_avg = COALESCE(v_avg_rating, 0.00),
        rating_count = COALESCE(v_count, 0)
    WHERE provider_id = NEW.provider_id;
END$$

-- ----------------------------------------------------------------------------
-- 2. TRIGGER: trg_after_review_update
-- Purpose: Updates provider rating when an existing review score is modified.
-- ----------------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_after_review_update$$

CREATE TRIGGER trg_after_review_update
AFTER UPDATE ON reviews
FOR EACH ROW
BEGIN
    DECLARE v_avg_rating DECIMAL(3, 2);
    DECLARE v_count INT;

    SELECT ROUND(AVG(rating), 2), COUNT(*)
    INTO v_avg_rating, v_count
    FROM reviews
    WHERE provider_id = NEW.provider_id;

    UPDATE service_providers
    SET rating_avg = COALESCE(v_avg_rating, 0.00),
        rating_count = COALESCE(v_count, 0)
    WHERE provider_id = NEW.provider_id;
END$$

-- ----------------------------------------------------------------------------
-- 3. TRIGGER: trg_before_booking_insert_guard
-- Purpose: Prevents slot collision and prevents booking past dates at the
-- database engine level, even if an application bypasses service layer checks.
-- ----------------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_before_booking_insert_guard$$

CREATE TRIGGER trg_before_booking_insert_guard
BEFORE INSERT ON bookings
FOR EACH ROW
BEGIN
    DECLARE v_existing_count INT DEFAULT 0;

    -- Guard: Booking in past
    IF NEW.booking_date < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'DB Constraint Guard: Cannot schedule a booking on a past date.';
    END IF;

    -- Guard: Double Booking / Slot collision
    SELECT COUNT(*) INTO v_existing_count
    FROM bookings
    WHERE provider_id = NEW.provider_id
      AND booking_date = NEW.booking_date
      AND slot_id = NEW.slot_id
      AND booking_status NOT IN ('CANCELLED', 'REJECTED');

    IF v_existing_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'DB Constraint Guard: Slot conflict detected. Provider is already booked.';
    END IF;
END$$

-- ----------------------------------------------------------------------------
-- 4. TRIGGER: trg_after_booking_status_audit
-- Purpose: Guarantees that EVERY booking state transition is captured in
-- booking_status_history table, maintaining an immutable historical timeline.
-- ----------------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_after_booking_status_audit$$

CREATE TRIGGER trg_after_booking_status_audit
AFTER UPDATE ON bookings
FOR EACH ROW
BEGIN
    IF OLD.booking_status != NEW.booking_status THEN
        INSERT INTO booking_status_history (
            booking_id, previous_status, new_status, remarks, changed_at
        ) VALUES (
            NEW.booking_id,
            OLD.booking_status,
            NEW.booking_status,
            CONCAT('Automatic audit: status updated to ', NEW.booking_status),
            CURRENT_TIMESTAMP
        );
    END IF;
END$$

DELIMITER ;
