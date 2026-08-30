# FixMate - Technical Architecture & Interview Defense Guide

This document equips developers to defend the design, architecture, and SQL engineering of **FixMate** during technical interviews.

---

## 1. Relational Database Design & SQL

### Q1: Why did you choose Spring JdbcTemplate over Spring Data JPA / Hibernate?
**Answer**:
1. **Explicit Query Performance & Predictability**: JPA often introduces N+1 select problems, lazy loading exceptions, and uncontrolled query cascades. With `JdbcTemplate`, every query is hand-tuned and parameterized, ensuring optimal execution plans.
2. **Advanced SQL Mastery**: FixMate demonstrates sophisticated SQL features like CTEs, Window functions (`ROW_NUMBER`, `DENSE_RANK`, `LAG`), Stored Procedures, and Triggers which are cumbersome or inefficient to express in JPA/HQL.
3. **Pessimistic Row-Level Locking**: High-concurrency slot booking requires explicit `SELECT ... FOR UPDATE` row locks, which map cleanly to JDBC transactions without ORM session overhead.

### Q2: How do you prevent double-booking at both the application and database levels?
**Answer**:
Double-booking defense is implemented across **three defensive tiers**:
1. **Application / DAO Level**: `bookingRepository.isSlotBooked()` checks if a non-cancelled booking exists for that `(provider_id, booking_date, slot_id)`.
2. **Database Trigger Guard**: `trg_before_booking_insert_guard` inspects incoming rows on `BEFORE INSERT` and raises SQLSTATE `'45000'` error if a conflict exists.
3. **ACID Transaction & Pessimistic Locking**: `sp_create_booking` uses `SELECT ... FOR UPDATE` within a `START TRANSACTION ... COMMIT` block to lock the provider's slot row during checkout.

### Q3: Explain how the 20 SQL analytics queries utilize Window functions.
**Answer**:
- **`DENSE_RANK() OVER (PARTITION BY city ORDER BY rating_avg DESC)`**: Ranks service providers within their respective cities without gaps in ranking numbers.
- **`LAG(gmv, 1) OVER (ORDER BY month)`**: Pulls the preceding month's revenue to compute exact Month-Over-Month (MoM) revenue growth percentages.
- **`SUM(amount) OVER (ORDER BY date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)`**: Computes running cumulative gross marketplace revenue over time.

---

## 2. Backend & Spring Security Architecture

### Q4: How is Role-Based Access Control (RBAC) enforced?
**Answer**:
1. Users have a defined `role` ENUM (`CUSTOMER`, `PROVIDER`, `ADMIN`).
2. Spring Security maps these roles to `GrantedAuthority` with `ROLE_` prefix in `UserPrincipal`.
3. Method security is enforced using `@PreAuthorize("hasRole('ADMIN')")` or `@PreAuthorize("hasRole('PROVIDER')")`.
4. URL pattern security is configured in `SecurityConfig` via `.authorizeHttpRequests()`.

### Q5: Why shouldn't pricing sent from the frontend client ever be trusted?
**Answer**:
A malicious user could tamper with the HTTP POST payload to book a ₹2000 AC repair for ₹1. In FixMate, the server ignores any client price. It retrieves authoritative pricing from `provider_services` (or fallback `services.base_price`), calculates platform commissions and GST server-side, and validates totals before creating payment orders.

---

## 3. Payments & Pluggable Architecture

### Q6: How does the payment abstraction work without hard external dependencies?
**Answer**:
The platform defines a `PaymentGatewayService` interface with `createOrder()`, `verifyPaymentSignature()`, and `processRefund()`. 
- For zero-cost local development and automated testing, `MockPaymentGatewayService` provides deterministic test checkouts.
- When `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET` are provided in environment variables, the system activates `RazorpayPaymentGatewayService`, performing HMAC-SHA256 signature verification.
