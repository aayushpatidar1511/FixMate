-- ============================================================================
-- FixMate - Local Service Booking & Management Platform
-- File: 06_functions.sql
-- Description: Stored Functions for Mathematical and Business Calculations
-- ============================================================================

USE fixmate_db;

DELIMITER $$

-- ----------------------------------------------------------------------------
-- 1. FUNCTION: fn_haversine_distance_km
-- Purpose: Calculates spherical distance between two GPS points in Kilometers.
-- Earth mean radius: 6371.0088 km.
-- ----------------------------------------------------------------------------
DROP FUNCTION IF EXISTS fn_haversine_distance_km$$

CREATE FUNCTION fn_haversine_distance_km(
    lat1 DECIMAL(10, 7),
    lon1 DECIMAL(10, 7),
    lat2 DECIMAL(10, 7),
    lon2 DECIMAL(10, 7)
)
RETURNS DECIMAL(8, 2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE dlat DOUBLE;
    DECLARE dlon DOUBLE;
    DECLARE a DOUBLE;
    DECLARE c DOUBLE;
    DECLARE d DOUBLE;
    DECLARE r DOUBLE DEFAULT 6371.0; -- Earth radius in KM

    IF lat1 IS NULL OR lon1 IS NULL OR lat2 IS NULL OR lon2 IS NULL THEN
        RETURN NULL;
    END IF;

    SET dlat = RADIANS(lat2 - lat1);
    SET dlon = RADIANS(lon2 - lon1);

    SET a = SIN(dlat / 2) * SIN(dlat / 2) +
            COS(RADIANS(lat1)) * COS(RADIANS(lat2)) *
            SIN(dlon / 2) * SIN(dlon / 2);

    SET c = 2 * ATAN2(SQRT(a), SQRT(1 - a));
    SET d = r * c;

    RETURN ROUND(d, 2);
END$$

-- ----------------------------------------------------------------------------
-- 2. FUNCTION: fn_calculate_commission
-- Purpose: Computes exact platform commission fee.
-- ----------------------------------------------------------------------------
DROP FUNCTION IF EXISTS fn_calculate_commission$$

CREATE FUNCTION fn_calculate_commission(
    p_amount DECIMAL(10, 2),
    p_percentage DECIMAL(5, 2)
)
RETURNS DECIMAL(10, 2)
DETERMINISTIC
NO SQL
BEGIN
    IF p_amount IS NULL OR p_amount <= 0 THEN
        RETURN 0.00;
    END IF;
    RETURN ROUND(p_amount * (p_percentage / 100.0), 2);
END$$

-- ----------------------------------------------------------------------------
-- 3. FUNCTION: fn_get_provider_completion_rate
-- Purpose: Returns historical fulfillment percentage for a given provider.
-- ----------------------------------------------------------------------------
DROP FUNCTION IF EXISTS fn_get_provider_completion_rate$$

CREATE FUNCTION fn_get_provider_completion_rate(
    p_provider_id BIGINT
)
RETURNS DECIMAL(5, 2)
READS SQL DATA
BEGIN
    DECLARE v_total INT;
    DECLARE v_completed INT;

    SELECT COUNT(*), SUM(CASE WHEN booking_status = 'COMPLETED' THEN 1 ELSE 0 END)
    INTO v_total, v_completed
    FROM bookings
    WHERE provider_id = p_provider_id AND booking_status NOT IN ('PENDING');

    IF v_total IS NULL OR v_total = 0 THEN
        RETURN 100.00;
    END IF;

    RETURN ROUND((v_completed / v_total) * 100.0, 2);
END$$

DELIMITER ;
