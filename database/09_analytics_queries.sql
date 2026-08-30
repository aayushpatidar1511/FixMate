-- ============================================================================
-- FixMate - Local Service Booking & Management Platform
-- File: 09_analytics_queries.sql
-- Description: 20 Advanced SQL Portfolio & Business Intelligence Analytics Queries
-- ============================================================================

USE fixmate_db;

-- ----------------------------------------------------------------------------
-- 1. Top 10 Providers by Completed Volume and Verified Rating
-- ----------------------------------------------------------------------------
SELECT 
    p.provider_id,
    u.full_name AS provider_name,
    p.city,
    p.rating_avg,
    p.rating_count,
    COUNT(b.booking_id) AS total_completed_jobs,
    SUM(b.provider_earnings) AS total_earnings
FROM service_providers p
JOIN users u ON p.user_id = u.user_id
JOIN bookings b ON p.provider_id = b.provider_id AND b.booking_status = 'COMPLETED'
GROUP BY p.provider_id, u.full_name, p.city, p.rating_avg, p.rating_count
ORDER BY total_completed_jobs DESC, p.rating_avg DESC
LIMIT 10;

-- ----------------------------------------------------------------------------
-- 2. Top 10 Services by Booking Frequency and Gross GMV
-- ----------------------------------------------------------------------------
SELECT 
    s.service_id,
    s.service_name,
    c.name AS category_name,
    COUNT(b.booking_id) AS booking_frequency,
    SUM(b.total_amount) AS gross_gmv,
    ROUND(AVG(b.total_amount), 2) AS average_ticket_size
FROM services s
JOIN categories c ON s.category_id = c.category_id
JOIN bookings b ON s.service_id = b.service_id
WHERE b.booking_status = 'COMPLETED'
GROUP BY s.service_id, s.service_name, c.name
ORDER BY booking_frequency DESC, gross_gmv DESC
LIMIT 10;

-- ----------------------------------------------------------------------------
-- 3. Highest Earning Providers (Gross Provider Payouts)
-- ----------------------------------------------------------------------------
SELECT 
    p.provider_id,
    u.full_name AS provider_name,
    p.city,
    p.wallet_balance AS current_wallet_balance,
    SUM(b.provider_earnings) AS lifetime_take_home_earnings,
    COUNT(b.booking_id) AS completed_orders
FROM service_providers p
JOIN users u ON p.user_id = u.user_id
JOIN bookings b ON p.provider_id = b.provider_id AND b.booking_status = 'COMPLETED'
GROUP BY p.provider_id, u.full_name, p.city, p.wallet_balance
ORDER BY lifetime_take_home_earnings DESC
LIMIT 10;

-- ----------------------------------------------------------------------------
-- 4. Monthly Revenue, Platform Commission & Tax Analytics
-- ----------------------------------------------------------------------------
SELECT 
    DATE_FORMAT(booking_date, '%Y-%m') AS report_month,
    COUNT(booking_id) AS total_orders,
    SUM(total_amount) AS total_gmv,
    SUM(platform_fee) AS platform_commission_net,
    SUM(tax_amount) AS gst_collected,
    SUM(provider_earnings) AS provider_disbursements
FROM bookings
WHERE booking_status = 'COMPLETED'
GROUP BY DATE_FORMAT(booking_date, '%Y-%m')
ORDER BY report_month DESC;

-- ----------------------------------------------------------------------------
-- 5. Monthly Booking Status Distribution Breakdown
-- ----------------------------------------------------------------------------
SELECT 
    DATE_FORMAT(booking_date, '%Y-%m') AS report_month,
    COUNT(*) AS total_created,
    SUM(CASE WHEN booking_status = 'COMPLETED' THEN 1 ELSE 0 END) AS count_completed,
    SUM(CASE WHEN booking_status = 'CANCELLED' THEN 1 ELSE 0 END) AS count_cancelled,
    SUM(CASE WHEN booking_status = 'REJECTED' THEN 1 ELSE 0 END) AS count_rejected,
    SUM(CASE WHEN booking_status IN ('PENDING', 'ACCEPTED', 'ON_THE_WAY', 'IN_PROGRESS') THEN 1 ELSE 0 END) AS count_active
FROM bookings
GROUP BY DATE_FORMAT(booking_date, '%Y-%m')
ORDER BY report_month DESC;

-- ----------------------------------------------------------------------------
-- 6. Customer Lifetime Value (CLV) Analysis
-- ----------------------------------------------------------------------------
SELECT 
    c.customer_id,
    u.full_name AS customer_name,
    u.email,
    COUNT(b.booking_id) AS completed_bookings,
    SUM(b.total_amount) AS lifetime_value,
    ROUND(AVG(b.total_amount), 2) AS average_spend_per_booking,
    MIN(b.booking_date) AS first_order_date,
    MAX(b.booking_date) AS latest_order_date
FROM customers c
JOIN users u ON c.user_id = u.user_id
JOIN bookings b ON c.customer_id = b.customer_id AND b.booking_status = 'COMPLETED'
GROUP BY c.customer_id, u.full_name, u.email
ORDER BY lifetime_value DESC
LIMIT 15;

-- ----------------------------------------------------------------------------
-- 7. Repeat Customers (Retention Rate Indicator)
-- ----------------------------------------------------------------------------
SELECT 
    c.customer_id,
    u.full_name AS customer_name,
    u.phone,
    COUNT(b.booking_id) AS total_orders,
    SUM(b.total_amount) AS total_spent
FROM customers c
JOIN users u ON c.user_id = u.user_id
JOIN bookings b ON c.customer_id = b.customer_id
WHERE b.booking_status = 'COMPLETED'
GROUP BY c.customer_id, u.full_name, u.phone
HAVING total_orders > 1
ORDER BY total_orders DESC, total_spent DESC;

-- ----------------------------------------------------------------------------
-- 8. Customers With Zero Bookings (Onboarding Funnel Drop-off)
-- ----------------------------------------------------------------------------
SELECT 
    c.customer_id,
    u.full_name,
    u.email,
    u.phone,
    u.created_at AS registered_at
FROM customers c
JOIN users u ON c.user_id = u.user_id
LEFT JOIN bookings b ON c.customer_id = b.customer_id
WHERE b.booking_id IS NULL
ORDER BY u.created_at DESC;

-- ----------------------------------------------------------------------------
-- 9. Average Provider Rating by Service Category
-- ----------------------------------------------------------------------------
SELECT 
    cat.category_id,
    cat.name AS category_name,
    COUNT(DISTINCT p.provider_id) AS active_providers,
    COUNT(r.review_id) AS total_reviews_received,
    ROUND(AVG(r.rating), 2) AS category_avg_rating
FROM categories cat
JOIN services s ON cat.category_id = s.category_id
JOIN provider_services ps ON s.service_id = ps.service_id
JOIN service_providers p ON ps.provider_id = p.provider_id
LEFT JOIN reviews r ON p.provider_id = r.provider_id
GROUP BY cat.category_id, cat.name
ORDER BY category_avg_rating DESC;

-- ----------------------------------------------------------------------------
-- 10. Most Cancelled Services Analysis
-- ----------------------------------------------------------------------------
SELECT 
    s.service_id,
    s.service_name,
    c.name AS category_name,
    COUNT(b.booking_id) AS total_requests,
    SUM(CASE WHEN b.booking_status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancellations,
    ROUND(
        (SUM(CASE WHEN b.booking_status = 'CANCELLED' THEN 1 ELSE 0 END) / COUNT(b.booking_id)) * 100, 
        2
    ) AS cancellation_rate_pct
FROM services s
JOIN categories c ON s.category_id = c.category_id
JOIN bookings b ON s.service_id = b.service_id
GROUP BY s.service_id, s.service_name, c.name
HAVING total_requests >= 2
ORDER BY cancellation_rate_pct DESC, cancellations DESC;

-- ----------------------------------------------------------------------------
-- 11. Platform & City-Level Cancellation Rate
-- ----------------------------------------------------------------------------
SELECT 
    p.city,
    COUNT(b.booking_id) AS total_bookings,
    SUM(CASE WHEN b.booking_status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_bookings,
    ROUND(
        (SUM(CASE WHEN b.booking_status = 'CANCELLED' THEN 1 ELSE 0 END) / COUNT(b.booking_id)) * 100, 
        2
    ) AS city_cancellation_pct
FROM bookings b
JOIN service_providers p ON b.provider_id = p.provider_id
GROUP BY p.city
ORDER BY total_bookings DESC;

-- ----------------------------------------------------------------------------
-- 12. Provider Fulfillment / Completion Rate
-- ----------------------------------------------------------------------------
SELECT 
    p.provider_id,
    u.full_name AS provider_name,
    p.city,
    COUNT(b.booking_id) AS total_assigned_jobs,
    SUM(CASE WHEN b.booking_status = 'COMPLETED' THEN 1 ELSE 0 END) AS fulfilled_jobs,
    ROUND(
        (SUM(CASE WHEN b.booking_status = 'COMPLETED' THEN 1 ELSE 0 END) / COUNT(b.booking_id)) * 100, 
        2
    ) AS fulfillment_rate_pct
FROM service_providers p
JOIN users u ON p.user_id = u.user_id
JOIN bookings b ON p.provider_id = b.provider_id
GROUP BY p.provider_id, u.full_name, p.city
HAVING total_assigned_jobs >= 3
ORDER BY fulfillment_rate_pct DESC, fulfilled_jobs DESC;

-- ----------------------------------------------------------------------------
-- 13. Gross Revenue by Service Category
-- ----------------------------------------------------------------------------
SELECT 
    cat.category_id,
    cat.name AS category_name,
    COUNT(b.booking_id) AS completed_orders,
    SUM(b.total_amount) AS category_gmv,
    SUM(b.platform_fee) AS category_platform_fee
FROM categories cat
JOIN services s ON cat.category_id = s.category_id
JOIN bookings b ON s.service_id = b.service_id AND b.booking_status = 'COMPLETED'
GROUP BY cat.category_id, cat.name
ORDER BY category_gmv DESC;

-- ----------------------------------------------------------------------------
-- 14. Gross Revenue and Market Share by City
-- ----------------------------------------------------------------------------
SELECT 
    p.city,
    COUNT(b.booking_id) AS completed_orders,
    SUM(b.total_amount) AS city_gmv,
    ROUND(
        (SUM(b.total_amount) / (SELECT SUM(total_amount) FROM bookings WHERE booking_status = 'COMPLETED')) * 100, 
        2
    ) AS market_share_percentage
FROM bookings b
JOIN service_providers p ON b.provider_id = p.provider_id
WHERE b.booking_status = 'COMPLETED'
GROUP BY p.city
ORDER BY city_gmv DESC;

-- ----------------------------------------------------------------------------
-- 15. Average Booking Value (AOV) by City & Category
-- ----------------------------------------------------------------------------
SELECT 
    p.city,
    cat.name AS category_name,
    COUNT(b.booking_id) AS order_volume,
    ROUND(AVG(b.total_amount), 2) AS average_order_value
FROM bookings b
JOIN service_providers p ON b.provider_id = p.provider_id
JOIN services s ON b.service_id = s.service_id
JOIN categories cat ON s.category_id = cat.category_id
WHERE b.booking_status = 'COMPLETED'
GROUP BY p.city, cat.name
ORDER BY p.city, average_order_value DESC;

-- ----------------------------------------------------------------------------
-- 16. Peak Booking Hours (Slot Utilization Heatmap)
-- ----------------------------------------------------------------------------
SELECT 
    sl.slot_id,
    sl.slot_name,
    sl.start_time,
    sl.end_time,
    COUNT(b.booking_id) AS total_slot_bookings,
    ROUND((COUNT(b.booking_id) / (SELECT COUNT(*) FROM bookings)) * 100, 2) AS slot_share_pct
FROM slots sl
LEFT JOIN bookings b ON sl.slot_id = b.slot_id
GROUP BY sl.slot_id, sl.slot_name, sl.start_time, sl.end_time
ORDER BY total_slot_bookings DESC;

-- ----------------------------------------------------------------------------
-- 17. Top Providers Per Service Using Window Function (DENSE_RANK)
-- ----------------------------------------------------------------------------
WITH RankedProviders AS (
    SELECT 
        s.service_name,
        u.full_name AS provider_name,
        p.city,
        p.rating_avg,
        COUNT(b.booking_id) AS jobs_done_for_service,
        DENSE_RANK() OVER (
            PARTITION BY s.service_id 
            ORDER BY COUNT(b.booking_id) DESC, p.rating_avg DESC
        ) as service_rank
    FROM services s
    JOIN bookings b ON s.service_id = b.service_id AND b.booking_status = 'COMPLETED'
    JOIN service_providers p ON b.provider_id = p.provider_id
    JOIN users u ON p.user_id = u.user_id
    GROUP BY s.service_id, s.service_name, p.provider_id, u.full_name, p.city, p.rating_avg
)
SELECT service_name, service_rank, provider_name, city, rating_avg, jobs_done_for_service
FROM RankedProviders
WHERE service_rank <= 3
ORDER BY service_name, service_rank;

-- ----------------------------------------------------------------------------
-- 18. Month-Over-Month (MoM) Revenue Growth Using LAG Window Function
-- ----------------------------------------------------------------------------
WITH MonthlyRevenue AS (
    SELECT 
        DATE_FORMAT(booking_date, '%Y-%m') AS rev_month,
        SUM(total_amount) AS current_month_gmv
    FROM bookings
    WHERE booking_status = 'COMPLETED'
    GROUP BY DATE_FORMAT(booking_date, '%Y-%m')
)
SELECT 
    rev_month,
    current_month_gmv,
    LAG(current_month_gmv, 1) OVER (ORDER BY rev_month ASC) AS previous_month_gmv,
    ROUND(
        ((current_month_gmv - LAG(current_month_gmv, 1) OVER (ORDER BY rev_month ASC)) /
         LAG(current_month_gmv, 1) OVER (ORDER BY rev_month ASC)) * 100, 
        2
    ) AS mom_growth_rate_pct
FROM MonthlyRevenue
ORDER BY rev_month ASC;

-- ----------------------------------------------------------------------------
-- 19. Running Revenue Cumulative Total Across Timeline
-- ----------------------------------------------------------------------------
SELECT 
    booking_date,
    SUM(total_amount) AS daily_revenue,
    SUM(SUM(total_amount)) OVER (ORDER BY booking_date ASC ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS cumulative_running_revenue
FROM bookings
WHERE booking_status = 'COMPLETED'
GROUP BY booking_date
ORDER BY booking_date ASC;

-- ----------------------------------------------------------------------------
-- 20. Provider Regional Ranking using DENSE_RANK() by City
-- ----------------------------------------------------------------------------
SELECT 
    p.city,
    DENSE_RANK() OVER (PARTITION BY p.city ORDER BY p.rating_avg DESC, p.total_completed_jobs DESC) AS city_rank,
    u.full_name AS provider_name,
    p.rating_avg,
    p.rating_count,
    p.total_completed_jobs,
    p.wallet_balance
FROM service_providers p
JOIN users u ON p.user_id = u.user_id
WHERE p.verification_status = 'ACTIVE'
ORDER BY p.city, city_rank ASC;
