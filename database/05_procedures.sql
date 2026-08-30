-- ============================================================================
-- FixMate - Local Service Booking & Management Platform
-- File: 05_procedures.sql
-- Description: ACID Stored Procedures for Critical Marketplace Transactions
-- ============================================================================

USE fixmate_db;

DELIMITER $$

-- ----------------------------------------------------------------------------
-- 1. PROCEDURE: sp_create_booking
-- Purpose: Atomically locks the slot, calculates server-verified financial
-- breakdown, creates booking record, and creates audit + notification entries.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_create_booking$$

CREATE PROCEDURE sp_create_booking(
    IN p_customer_id BIGINT,
    IN p_provider_id BIGINT,
    IN p_service_id BIGINT,
    IN p_address_id BIGINT,
    IN p_booking_date DATE,
    IN p_slot_id BIGINT,
    IN p_problem_description TEXT,
    OUT p_booking_id BIGINT,
    OUT p_booking_number VARCHAR(30),
    OUT p_total_amount DECIMAL(10, 2)
)
proc_label: BEGIN
    DECLARE v_provider_status VARCHAR(30);
    DECLARE v_slot_conflict_count INT DEFAULT 0;
    DECLARE v_base_price DECIMAL(10, 2);
    DECLARE v_platform_fee DECIMAL(10, 2);
    DECLARE v_tax_amount DECIMAL(10, 2);
    DECLARE v_provider_user_id BIGINT;
    DECLARE v_customer_user_id BIGINT;
    DECLARE v_generated_bnum VARCHAR(30);

    -- Standard error handler for rollback
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- Validate booking date is not in the past
    IF p_booking_date < CURDATE() THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Validation Error: Booking date cannot be in the past.';
    END IF;

    -- 1. Verify Provider is ACTIVE
    SELECT verification_status, user_id INTO v_provider_status, v_provider_user_id
    FROM service_providers
    WHERE provider_id = p_provider_id
    FOR SHARE;

    IF v_provider_status IS NULL THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Not Found: Service provider does not exist.';
    END IF;

    IF v_provider_status != 'ACTIVE' THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Invalid State: Service provider is not currently active for bookings.';
    END IF;

    -- 2. Concurrency Lock & Slot Collision Check (Pessimistic Locking)
    SELECT COUNT(*) INTO v_slot_conflict_count
    FROM bookings
    WHERE provider_id = p_provider_id 
      AND booking_date = p_booking_date 
      AND slot_id = p_slot_id 
      AND booking_status NOT IN ('CANCELLED', 'REJECTED')
    FOR UPDATE;

    IF v_slot_conflict_count > 0 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Slot Conflict: Selected time slot is already booked for this provider.';
    END IF;

    -- 3. Retrieve Authoritative Price (Custom Provider Price or Service Base Price)
    SELECT COALESCE(ps.custom_price, s.base_price) INTO v_base_price
    FROM services s
    LEFT JOIN provider_services ps ON s.service_id = ps.service_id AND ps.provider_id = p_provider_id
    WHERE s.service_id = p_service_id;

    IF v_base_price IS NULL THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Pricing Error: Service pricing not found.';
    END IF;

    -- 4. Calculate Server-Side Breakdown
    SET v_platform_fee = ROUND(v_base_price * 0.10, 2);  -- 10% platform commission
    SET v_tax_amount = ROUND(v_platform_fee * 0.18, 2);   -- 18% GST on platform fee
    SET p_total_amount = v_base_price + v_platform_fee + v_tax_amount;

    -- 5. Generate Unique Booking Number
    SET v_generated_bnum = CONCAT('FM-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', LPAD(FLOOR(RAND() * 89999 + 10000), 5, '0'));
    SET p_booking_number = v_generated_bnum;

    -- 6. Insert Booking Record
    INSERT INTO bookings (
        booking_number, customer_id, provider_id, service_id, address_id,
        booking_date, slot_id, problem_description, base_amount, platform_fee,
        tax_amount, discount_amount, total_amount, provider_earnings,
        booking_status, payment_status
    ) VALUES (
        v_generated_bnum, p_customer_id, p_provider_id, p_service_id, p_address_id,
        p_booking_date, p_slot_id, p_problem_description, v_base_price, v_platform_fee,
        v_tax_amount, 0.00, p_total_amount, v_base_price,
        'PENDING', 'PENDING'
    );

    SET p_booking_id = LAST_INSERT_ID();

    -- 7. Audit History
    SELECT user_id INTO v_customer_user_id FROM customers WHERE customer_id = p_customer_id;

    INSERT INTO booking_status_history (booking_id, previous_status, new_status, changed_by_user_id, remarks)
    VALUES (p_booking_id, NULL, 'PENDING', v_customer_user_id, 'Booking created by customer');

    -- 8. Real-time Notification for Provider
    INSERT INTO notifications (user_id, title, message, type, reference_id)
    VALUES (
        v_provider_user_id, 
        'New Job Request', 
        CONCAT('New service booking ', v_generated_bnum, ' requested for date ', p_booking_date), 
        'BOOKING_UPDATE', 
        p_booking_id
    );

    COMMIT;
END proc_label$$

-- ----------------------------------------------------------------------------
-- 2. PROCEDURE: sp_complete_booking_payout
-- Purpose: Marks booking COMPLETED, credits provider wallet, writes ledger,
-- and increments total completed jobs counter.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_complete_booking_payout$$

CREATE PROCEDURE sp_complete_booking_payout(
    IN p_booking_id BIGINT,
    IN p_provider_id BIGINT
)
BEGIN
    DECLARE v_current_status VARCHAR(30);
    DECLARE v_provider_earnings DECIMAL(10, 2);
    DECLARE v_new_balance DECIMAL(12, 2);
    DECLARE v_booking_number VARCHAR(30);
    DECLARE v_customer_user_id BIGINT;
    DECLARE v_provider_user_id BIGINT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- Validate booking state
    SELECT b.booking_status, b.provider_earnings, b.booking_number, c.user_id, p.user_id
    INTO v_current_status, v_provider_earnings, v_booking_number, v_customer_user_id, v_provider_user_id
    FROM bookings b
    JOIN customers c ON b.customer_id = c.customer_id
    JOIN service_providers p ON b.provider_id = p.provider_id
    WHERE b.booking_id = p_booking_id AND b.provider_id = p_provider_id
    FOR UPDATE;

    IF v_current_status IS NULL THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Booking not found or does not belong to specified provider.';
    END IF;

    IF v_current_status = 'COMPLETED' THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Booking has already been completed and paid out.';
    END IF;

    IF v_current_status NOT IN ('ACCEPTED', 'ON_THE_WAY', 'IN_PROGRESS') THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Invalid state transition: Booking must be in progress to be completed.';
    END IF;

    -- 1. Update Booking Status
    UPDATE bookings 
    SET booking_status = 'COMPLETED', updated_at = CURRENT_TIMESTAMP
    WHERE booking_id = p_booking_id;

    -- 2. Update Provider Wallet and Counters
    UPDATE service_providers 
    SET wallet_balance = wallet_balance + v_provider_earnings,
        total_completed_jobs = total_completed_jobs + 1
    WHERE provider_id = p_provider_id;

    -- Retrieve updated running balance
    SELECT wallet_balance INTO v_new_balance 
    FROM service_providers 
    WHERE provider_id = p_provider_id;

    -- 3. Write Ledger Entry
    INSERT INTO provider_wallet_ledger (
        provider_id, booking_id, transaction_type, amount, running_balance, description
    ) VALUES (
        p_provider_id, p_booking_id, 'CREDIT_BOOKING_PAYOUT', v_provider_earnings, v_new_balance,
        CONCAT('Payout credited for completed service ', v_booking_number)
    );

    -- 4. Status History
    INSERT INTO booking_status_history (booking_id, previous_status, new_status, changed_by_user_id, remarks)
    VALUES (p_booking_id, v_current_status, 'COMPLETED', v_provider_user_id, 'Technician concluded job successfully');

    -- 5. Customer Notification (Prompting Review)
    INSERT INTO notifications (user_id, title, message, type, reference_id)
    VALUES (
        v_customer_user_id,
        'Service Completed',
        CONCAT('Your service ', v_booking_number, ' is marked complete. Please share your rating and review!'),
        'BOOKING_UPDATE',
        p_booking_id
    );

    COMMIT;
END$$

-- ----------------------------------------------------------------------------
-- 3. PROCEDURE: sp_cancel_booking_and_refund
-- Purpose: Handles cancellation rules, updates status, and queues refund.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_cancel_booking_and_refund$$

CREATE PROCEDURE sp_cancel_booking_and_refund(
    IN p_booking_id BIGINT,
    IN p_cancelled_by VARCHAR(20), -- 'CUSTOMER', 'PROVIDER', 'ADMIN'
    IN p_reason VARCHAR(255),
    IN p_changed_by_user_id BIGINT
)
BEGIN
    DECLARE v_current_status VARCHAR(30);
    DECLARE v_payment_status VARCHAR(30);
    DECLARE v_total_amount DECIMAL(10, 2);
    DECLARE v_payment_id BIGINT;
    DECLARE v_customer_user_id BIGINT;
    DECLARE v_provider_user_id BIGINT;
    DECLARE v_booking_number VARCHAR(30);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    SELECT b.booking_status, b.payment_status, b.total_amount, b.booking_number,
           c.user_id, p.user_id, pay.payment_id
    INTO v_current_status, v_payment_status, v_total_amount, v_booking_number,
         v_customer_user_id, v_provider_user_id, v_payment_id
    FROM bookings b
    JOIN customers c ON b.customer_id = c.customer_id
    JOIN service_providers p ON b.provider_id = p.provider_id
    LEFT JOIN payments pay ON b.booking_id = pay.booking_id AND pay.status = 'SUCCESS'
    WHERE b.booking_id = p_booking_id
    FOR UPDATE;

    IF v_current_status IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Booking not found.';
    END IF;

    IF v_current_status IN ('COMPLETED', 'CANCELLED', 'REJECTED') THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Booking cannot be cancelled from its current terminal status.';
    END IF;

    -- Update booking status
    UPDATE bookings 
    SET booking_status = 'CANCELLED',
        cancellation_reason = p_reason,
        cancelled_by = p_cancelled_by,
        payment_status = CASE WHEN v_payment_status = 'PAID' THEN 'REFUNDED' ELSE payment_status END,
        updated_at = CURRENT_TIMESTAMP
    WHERE booking_id = p_booking_id;

    -- If customer has already paid, trigger refund entry
    IF v_payment_status = 'PAID' AND v_payment_id IS NOT NULL THEN
        INSERT INTO refunds (booking_id, payment_id, amount, reason, status, processed_at)
        VALUES (p_booking_id, v_payment_id, v_total_amount, p_reason, 'PROCESSED', CURRENT_TIMESTAMP);

        UPDATE payments SET status = 'REFUNDED' WHERE payment_id = v_payment_id;
    END IF;

    -- Audit history
    INSERT INTO booking_status_history (booking_id, previous_status, new_status, changed_by_user_id, remarks)
    VALUES (p_booking_id, v_current_status, 'CANCELLED', p_changed_by_user_id, CONCAT('Cancelled: ', p_reason));

    -- Notify relevant party
    IF p_cancelled_by = 'CUSTOMER' THEN
        INSERT INTO notifications (user_id, title, message, type, reference_id)
        VALUES (v_provider_user_id, 'Booking Cancelled', CONCAT('Booking ', v_booking_number, ' was cancelled by customer.'), 'BOOKING_UPDATE', p_booking_id);
    ELSE
        INSERT INTO notifications (user_id, title, message, type, reference_id)
        VALUES (v_customer_user_id, 'Booking Cancelled', CONCAT('Booking ', v_booking_number, ' was cancelled. Refund processed if prepaid.'), 'BOOKING_UPDATE', p_booking_id);
    END IF;

    COMMIT;
END$$

DELIMITER ;
