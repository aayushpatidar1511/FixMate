-- ============================================================================
-- FixMate - Local Service Booking & Management Platform
-- File: 08_sample_queries.sql
-- Description: Core SQL Mastery Demonstrations (Joins, Subqueries, CTEs, Transactions)
-- ============================================================================

USE fixmate_db;

-- ----------------------------------------------------------------------------
-- 1. INNER JOIN
-- Scenario: Retrieve full dispatch details for all currently ongoing jobs.
-- ----------------------------------------------------------------------------
SELECT 
    b.booking_number,
    b.booking_date,
    sl.slot_name,
    s.service_name,
    c_usr.full_name AS customer_name,
    c_usr.phone AS customer_phone,
    a.street_address,
    a.city,
    p_usr.full_name AS provider_name,
    p_usr.phone AS provider_phone,
    b.total_amount
FROM bookings b
INNER JOIN customers c ON b.customer_id = c.customer_id
INNER JOIN users c_usr ON c.user_id = c_usr.user_id
INNER JOIN service_providers p ON b.provider_id = p.provider_id
INNER JOIN users p_usr ON p.user_id = p_usr.user_id
INNER JOIN services s ON b.service_id = s.service_id
INNER JOIN addresses a ON b.address_id = a.address_id
INNER JOIN slots sl ON b.slot_id = sl.slot_id
WHERE b.booking_status IN ('ACCEPTED', 'ON_THE_WAY', 'IN_PROGRESS')
ORDER BY b.booking_date ASC;

-- ----------------------------------------------------------------------------
-- 2. LEFT JOIN
-- Scenario: Audit all providers including newly onboarded ones who haven't yet
-- fulfilled any customer booking.
-- ----------------------------------------------------------------------------
SELECT 
    p.provider_id,
    u.full_name AS provider_name,
    p.city,
    p.verification_status,
    p.rating_avg,
    COUNT(b.booking_id) AS total_bookings_received,
    COALESCE(SUM(CASE WHEN b.booking_status = 'COMPLETED' THEN b.provider_earnings ELSE 0 END), 0.00) AS total_earned
FROM service_providers p
JOIN users u ON p.user_id = u.user_id
LEFT JOIN bookings b ON p.provider_id = b.provider_id
GROUP BY p.provider_id, u.full_name, p.city, p.verification_status, p.rating_avg
ORDER BY total_bookings_received ASC;

-- ----------------------------------------------------------------------------
-- 3. RIGHT JOIN
-- Scenario: Verify category coverage and detect categories that have zero services.
-- ----------------------------------------------------------------------------
SELECT 
    c.category_id,
    c.name AS category_name,
    s.service_id,
    s.service_name,
    s.base_price
FROM services s
RIGHT JOIN categories c ON s.category_id = c.category_id
ORDER BY c.category_id, s.service_name;

-- ----------------------------------------------------------------------------
-- 4. SELF JOIN
-- Scenario: Pair providers located in the same city to identify experience peer groups.
-- ----------------------------------------------------------------------------
SELECT 
    p1.city,
    u1.full_name AS senior_provider,
    p1.experience_years AS senior_years,
    u2.full_name AS junior_provider,
    p2.experience_years AS junior_years,
    (p1.experience_years - p2.experience_years) AS experience_gap_years
FROM service_providers p1
JOIN service_providers p2 ON p1.city = p2.city AND p1.provider_id != p2.provider_id AND p1.experience_years > p2.experience_years
JOIN users u1 ON p1.user_id = u1.user_id
JOIN users u2 ON p2.user_id = u2.user_id
WHERE p1.city = 'Ujjain'
ORDER BY experience_gap_years DESC
LIMIT 10;

-- ----------------------------------------------------------------------------
-- 5. GROUP BY & HAVING
-- Scenario: Top Tier Elite Providers (completed >= 5 jobs with avg rating >= 4.80).
-- ----------------------------------------------------------------------------
SELECT 
    p.provider_id,
    u.full_name AS provider_name,
    p.city,
    COUNT(b.booking_id) AS completed_jobs,
    ROUND(AVG(r.rating), 2) AS verified_customer_rating,
    SUM(b.provider_earnings) AS total_payout
FROM service_providers p
JOIN users u ON p.user_id = u.user_id
JOIN bookings b ON p.provider_id = b.provider_id AND b.booking_status = 'COMPLETED'
LEFT JOIN reviews r ON b.booking_id = r.booking_id
GROUP BY p.provider_id, u.full_name, p.city
HAVING completed_jobs >= 5 AND verified_customer_rating >= 4.80
ORDER BY verified_customer_rating DESC, total_payout DESC;

-- ----------------------------------------------------------------------------
-- 6. SUBQUERY IN WHERE CLAUSE (Scalar Subquery)
-- Scenario: Identify High-Value Customers spending above the platform mean.
-- ----------------------------------------------------------------------------
SELECT 
    c.customer_id,
    u.full_name AS customer_name,
    u.email,
    SUM(b.total_amount) AS total_customer_spent
FROM customers c
JOIN users u ON c.user_id = u.user_id
JOIN bookings b ON c.customer_id = b.customer_id AND b.booking_status = 'COMPLETED'
GROUP BY c.customer_id, u.full_name, u.email
HAVING total_customer_spent > (
    -- Subquery: Overall average customer spend
    SELECT AVG(customer_spend) 
    FROM (
        SELECT SUM(total_amount) AS customer_spend 
        FROM bookings 
        WHERE booking_status = 'COMPLETED' 
        GROUP BY customer_id
    ) AS avg_spend_sub
)
ORDER BY total_customer_spent DESC;

-- ----------------------------------------------------------------------------
-- 7. CORRELATED SUBQUERY
-- Scenario: For each service, find the provider offering the lowest custom rate.
-- ----------------------------------------------------------------------------
SELECT 
    s.service_name,
    s.base_price,
    ps.custom_price AS best_custom_price,
    u.full_name AS recommended_provider,
    p.city
FROM provider_services ps
JOIN services s ON ps.service_id = s.service_id
JOIN service_providers p ON ps.provider_id = p.provider_id
JOIN users u ON p.user_id = u.user_id
WHERE ps.custom_price = (
    -- Correlated Subquery: Minimum price for this specific service
    SELECT MIN(sub_ps.custom_price)
    FROM provider_services sub_ps
    WHERE sub_ps.service_id = ps.service_id
      AND sub_ps.is_available = TRUE
)
ORDER BY s.service_name;

-- ----------------------------------------------------------------------------
-- 8. COMMON TABLE EXPRESSIONS (CTEs)
-- Scenario: Multi-Stage City & Category Financial Performance Matrix
-- ----------------------------------------------------------------------------
WITH CityMetrics AS (
    SELECT 
        p.city,
        COUNT(b.booking_id) AS city_bookings,
        SUM(b.total_amount) AS city_gross_revenue,
        SUM(b.platform_fee) AS city_platform_profit
    FROM bookings b
    JOIN service_providers p ON b.provider_id = p.provider_id
    WHERE b.booking_status = 'COMPLETED'
    GROUP BY p.city
),
OverallTotal AS (
    SELECT SUM(city_gross_revenue) AS total_market_revenue FROM CityMetrics
)
SELECT 
    cm.city,
    cm.city_bookings,
    cm.city_gross_revenue,
    cm.city_platform_profit,
    ROUND((cm.city_gross_revenue / ot.total_market_revenue) * 100, 2) AS revenue_contribution_pct
FROM CityMetrics cm
CROSS JOIN OverallTotal ot
ORDER BY cm.city_gross_revenue DESC;

-- ----------------------------------------------------------------------------
-- 9. TRANSACTION ACID WORKFLOW (Demonstration of COMMIT & ROLLBACK)
-- ----------------------------------------------------------------------------

-- A. Successful Flow Simulation (COMMIT)
START TRANSACTION;

-- Step 1: Lock and reserve slot
INSERT INTO bookings (
    booking_number, customer_id, provider_id, service_id, address_id,
    booking_date, slot_id, problem_description, base_amount, platform_fee,
    tax_amount, total_amount, provider_earnings, booking_status, payment_status
) VALUES (
    'FM-DEMO-ACID-01', 1, 1, 1, 1,
    DATE_ADD(CURDATE(), INTERVAL 5 DAY), 3, 'ACID testing transaction flow', 
    249.00, 24.90, 4.48, 278.38, 249.00, 'PENDING', 'PAID'
);

SET @last_demo_booking_id = LAST_INSERT_ID();

-- Step 2: Create Payment
INSERT INTO payments (
    booking_id, transaction_reference, payment_method, payment_gateway,
    amount, status, paid_at
) VALUES (
    @last_demo_booking_id, 'TXN-DEMO-ACID-001', 'UPI', 'MOCK',
    278.38, 'SUCCESS', CURRENT_TIMESTAMP
);

-- Step 3: Commit all changes atomically
COMMIT;

-- B. Intentional Failure Simulation (ROLLBACK)
START TRANSACTION;

INSERT INTO bookings (
    booking_number, customer_id, provider_id, service_id, address_id,
    booking_date, slot_id, problem_description, base_amount, platform_fee,
    tax_amount, total_amount, provider_earnings, booking_status, payment_status
) VALUES (
    'FM-DEMO-FAIL-01', 1, 1, 1, 1,
    DATE_ADD(CURDATE(), INTERVAL 6 DAY), 4, 'Simulated payment gateway timeout', 
    249.00, 24.90, 4.48, 278.38, 249.00, 'PENDING', 'PENDING'
);

-- Simulating payment error: Abort and rollback entire transaction
ROLLBACK;
