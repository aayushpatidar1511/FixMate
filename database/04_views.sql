-- ============================================================================
-- FixMate - Local Service Booking & Management Platform
-- File: 04_views.sql
-- Description: Business Reporting Views & Analytics Abstractions
-- ============================================================================

USE fixmate_db;

-- ----------------------------------------------------------------------------
-- 1. VIEW: Comprehensive Provider Performance Summary
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_provider_performance_summary AS
SELECT 
    p.provider_id,
    u.full_name AS provider_name,
    u.phone AS provider_phone,
    u.email AS provider_email,
    p.city,
    p.verification_status,
    p.experience_years,
    p.rating_avg,
    p.rating_count,
    p.total_completed_jobs,
    p.wallet_balance,
    COUNT(b.booking_id) AS total_assigned_bookings,
    SUM(CASE WHEN b.booking_status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_bookings_count,
    SUM(CASE WHEN b.booking_status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_bookings_count,
    SUM(CASE WHEN b.booking_status = 'REJECTED' THEN 1 ELSE 0 END) AS rejected_bookings_count,
    ROUND(
        (SUM(CASE WHEN b.booking_status = 'COMPLETED' THEN 1 ELSE 0 END) / 
        NULLIF(COUNT(b.booking_id), 0)) * 100, 
        2
    ) AS completion_rate_percentage,
    COALESCE(SUM(CASE WHEN b.booking_status = 'COMPLETED' THEN b.provider_earnings ELSE 0 END), 0.00) AS total_lifetime_earnings
FROM service_providers p
JOIN users u ON p.user_id = u.user_id
LEFT JOIN bookings b ON p.provider_id = b.provider_id
GROUP BY p.provider_id, u.full_name, u.phone, u.email, p.city, p.verification_status, 
         p.experience_years, p.rating_avg, p.rating_count, p.total_completed_jobs, p.wallet_balance;

-- ----------------------------------------------------------------------------
-- 2. VIEW: Booking Full Detail Consolidated View
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_booking_full_detail AS
SELECT 
    b.booking_id,
    b.booking_number,
    b.booking_date,
    b.booking_status,
    b.payment_status,
    b.problem_description,
    b.base_amount,
    b.platform_fee,
    b.tax_amount,
    b.total_amount,
    b.provider_earnings,
    b.cancellation_reason,
    b.cancelled_by,
    b.created_at AS booking_created_at,
    -- Customer Info
    c.customer_id,
    cu.full_name AS customer_name,
    cu.phone AS customer_phone,
    cu.email AS customer_email,
    -- Address Info
    a.label AS address_label,
    a.street_address,
    a.landmark,
    a.city AS service_city,
    a.pincode AS service_pincode,
    -- Service & Category
    s.service_id,
    s.service_name,
    cat.category_id,
    cat.name AS category_name,
    -- Provider Info
    p.provider_id,
    pu.full_name AS provider_name,
    pu.phone AS provider_phone,
    p.rating_avg AS provider_rating,
    -- Slot Info
    sl.slot_id,
    sl.slot_name,
    sl.start_time,
    sl.end_time,
    -- Payment Info
    pay.payment_id,
    pay.transaction_reference,
    pay.payment_method,
    pay.status AS payment_gateway_status,
    pay.paid_at
FROM bookings b
JOIN customers c ON b.customer_id = c.customer_id
JOIN users cu ON c.user_id = cu.user_id
JOIN addresses a ON b.address_id = a.address_id
JOIN services s ON b.service_id = s.service_id
JOIN categories cat ON s.category_id = cat.category_id
JOIN service_providers p ON b.provider_id = p.provider_id
JOIN users pu ON p.user_id = pu.user_id
JOIN slots sl ON b.slot_id = sl.slot_id
LEFT JOIN payments pay ON b.booking_id = pay.booking_id;

-- ----------------------------------------------------------------------------
-- 3. VIEW: Monthly Financial Analytics & Revenue Reconciliation
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_monthly_financial_analytics AS
SELECT 
    DATE_FORMAT(b.booking_date, '%Y-%m') AS booking_month,
    COUNT(b.booking_id) AS total_bookings,
    SUM(CASE WHEN b.booking_status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_bookings,
    SUM(CASE WHEN b.booking_status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_bookings,
    COALESCE(SUM(b.total_amount), 0.00) AS gross_merchandise_value,
    COALESCE(SUM(CASE WHEN b.booking_status = 'COMPLETED' THEN b.base_amount ELSE 0 END), 0.00) AS provider_base_gross,
    COALESCE(SUM(CASE WHEN b.booking_status = 'COMPLETED' THEN b.platform_fee ELSE 0 END), 0.00) AS platform_revenue_earned,
    COALESCE(SUM(CASE WHEN b.booking_status = 'COMPLETED' THEN b.tax_amount ELSE 0 END), 0.00) AS gst_tax_collected,
    COALESCE(SUM(CASE WHEN b.booking_status = 'COMPLETED' THEN b.provider_earnings ELSE 0 END), 0.00) AS total_provider_payouts,
    COALESCE(SUM(CASE WHEN b.payment_status = 'REFUNDED' THEN b.total_amount ELSE 0 END), 0.00) AS total_refunds_issued
FROM bookings b
GROUP BY DATE_FORMAT(b.booking_date, '%Y-%m');

-- ----------------------------------------------------------------------------
-- 4. VIEW: Customer Lifetime Value (CLV) & Retention Metrics
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_customer_lifetime_metrics AS
SELECT 
    c.customer_id,
    u.full_name AS customer_name,
    u.email AS customer_email,
    u.phone AS customer_phone,
    u.created_at AS customer_since,
    COUNT(b.booking_id) AS total_bookings_placed,
    SUM(CASE WHEN b.booking_status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_bookings,
    SUM(CASE WHEN b.booking_status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_bookings,
    COALESCE(SUM(CASE WHEN b.booking_status = 'COMPLETED' THEN b.total_amount ELSE 0 END), 0.00) AS lifetime_expenditure,
    ROUND(AVG(CASE WHEN b.booking_status = 'COMPLETED' THEN b.total_amount ELSE NULL END), 2) AS average_order_value,
    MIN(b.booking_date) AS first_booking_date,
    MAX(b.booking_date) AS most_recent_booking_date
FROM customers c
JOIN users u ON c.user_id = u.user_id
LEFT JOIN bookings b ON c.customer_id = b.customer_id
GROUP BY c.customer_id, u.full_name, u.email, u.phone, u.created_at;
