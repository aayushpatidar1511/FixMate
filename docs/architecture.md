# FixMate - System Architecture & Technical Specification

## 1. System Overview

**FixMate** is a production-grade, multi-tenant local service booking and management platform designed to connect verified service professionals (electricians, plumbers, AC mechanics, carpenters, cleaners) with residential and commercial customers.

The system is architected for:
- **High Concurrency & ACID Integrity**: Slot reservation and payment reconciliation are protected by database transactions, pessimistic/optimistic checks, and constraint-level triggers to guarantee zero double-bookings.
- **Geo-Proximity Matchmaking**: Dynamic search and ranking of service providers using spherical distance (Haversine formula) based on GPS coordinates and city/pincode fallbacks.
- **Dual Payment Capability**: Pluggable payment interface supporting an offline Mock Payment Provider for zero-dependency local development and testing, alongside live/sandbox Razorpay integration.
- **Auditable Lifecycle Workflows**: Complete status tracking for bookings, refunds, provider wallet disbursements, and dispute settlements.

---

## 2. Architecture Diagram

```
+----------------------------------------------------------------------------------------------------+
|                                    CLIENT TIER (Modern Web UI)                                     |
|  - HTML5 / CSS3 (Glassmorphism, Dark/Light Tokens) / Modern Vanilla JS (Modular ES6+)              |
|  - Customer Portal: Discovery, Map/List, Multi-step Booking Wizard, Live Tracker, Reviews          |
|  - Provider Portal: Availability Slot Grid, Job Acceptance Desk, Job State Stepper, Wallet Ledger  |
|  - Admin Portal: Verification Desk, Service/Category CRUD, Financial Reports, System Disputes      |
+----------------------------------------------------------------------------------------------------+
                                                  │
                                                  │ HTTPS / JSON REST API
                                                  ▼
+----------------------------------------------------------------------------------------------------+
|                                  APPLICATION TIER (Spring Boot 3)                                  |
|                                                                                                    |
|  [Security & Auth Layer]                                                                           |
|   ├── JwtAuthenticationFilter (Stateless Bearer token validation)                                  |
|   ├── SecurityFilterChain (CORS policy, URL permission matrices)                                   |
|   └── UserPrincipalDetailsService (Role-based: CUSTOMER, PROVIDER, ADMIN)                         |
|                                                                                                    |
|  [Presentation Layer - Controllers]                                                                |
|   ├── AuthController, CategoryController, ServiceController, ProviderController                     |
|   ├── BookingController, PaymentController, ReviewController, ComplaintController                   |
|   └── NotificationController, AdminController                                                      |
|                                                                                                    |
|  [Business Logic Layer - Services]                                                                 |
|   ├── BookingEngineService (Slot collision detection, pricing calculation, state machine)          |
|   ├── ProviderService (Haversine proximity filter, schedule management)                            |
|   ├── PaymentGatewayService Abstraction                                                            |
|   │     ├── MockPaymentGatewayService (Deterministic local testing)                                |
|   │     └── RazorpayPaymentGatewayService (Razorpay Webhook & Signature Verification)              |
|   ├── WalletLedgerService (Provider payout accounting, commission deduction)                       |
|   └── CentralizedExceptionHandler (@RestControllerAdvice, uniform ApiResponse<T>)                  |
|                                                                                                    |
|  [Persistence Layer - DAOs & Repositories]                                                         |
|   ├── Spring NamedParameterJdbcTemplate & JdbcTemplate                                             |
|   ├── Parameterized PreparedStatements (100% SQL Injection Defense)                                |
|   └── Custom Typed RowMappers (Domain entity binding)                                              |
+----------------------------------------------------------------------------------------------------+
                                                  │
                                                  │ TCP / JDBC Connection Pool (HikariCP)
                                                  ▼
+----------------------------------------------------------------------------------------------------+
|                                  DATABASE TIER (MySQL 8.0 Enterprise)                               |
|                                                                                                    |
|  [Relational Core - 17 Tables]                                                                     |
|   users, customers, service_providers, addresses, categories, services, provider_services,         |
|   slots, provider_slots, bookings, booking_status_history, payments, refunds, reviews,             |
|   complaints, notifications, provider_wallet_ledger                                                |
|                                                                                                    |
|  [Data Integrity & Automation]                                                                     |
|   ├── Foreign Keys with Strict ON DELETE/UPDATE rules & ENUM / CHECK constraints                   |
|   ├── Stored Procedures (sp_book_service, sp_complete_booking_payout, sp_process_cancellation)     |
|   ├── Stored Functions (fn_haversine_distance_km, fn_calculate_commission)                         |
|   ├── Database Triggers (trg_after_review_update_rating, trg_after_booking_status_audit)           |
|   └── Analytics Views (v_provider_performance_summary, v_monthly_financial_analytics)              |
+----------------------------------------------------------------------------------------------------+
```

---

## 3. Package Structure

```
com.fixmate
├── FixMateApplication.java
├── config
│   ├── AppProperties.java
│   ├── CorsConfig.java
│   ├── JdbcConfig.java
│   └── SecurityConfig.java
├── controller
│   ├── AdminController.java
│   ├── AuthController.java
│   ├── BookingController.java
│   ├── CategoryController.java
│   ├── ComplaintController.java
│   ├── NotificationController.java
│   ├── PaymentController.java
│   ├── ProviderController.java
│   ├── ReviewController.java
│   └── ServiceController.java
├── dto
│   ├── request
│   │   ├── BookingCreateRequest.java
│   │   ├── ComplaintRequest.java
│   │   ├── LoginRequest.java
│   │   ├── PaymentVerificationRequest.java
│   │   ├── ProviderRegistrationRequest.java
│   │   ├── ReviewRequest.java
│   │   ├── SlotConfigDto.java
│   │   └── UserRegistrationRequest.java
│   └── response
│       ├── ApiResponse.java
│       ├── AuthResponse.java
│       ├── BookingSummaryResponse.java
│       ├── DashboardStatsResponse.java
│       └── ProviderCardResponse.java
├── exception
│   ├── ApiException.java
│   ├── BadRequestException.java
│   ├── ConflictException.java
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
├── model
│   ├── Address.java
│   ├── Booking.java
│   ├── BookingStatusHistory.java
│   ├── Category.java
│   ├── Complaint.java
│   ├── Customer.java
│   ├── Notification.java
│   ├── Payment.java
│   ├── Provider.java
│   ├── ProviderServiceItem.java
│   ├── Refund.java
│   ├── Review.java
│   ├── ServiceEntity.java
│   ├── Slot.java
│   ├── User.java
│   └── WalletLedger.java
├── payment
│   ├── PaymentGatewayService.java
│   ├── MockPaymentGatewayService.java
│   ├── RazorpayPaymentGatewayService.java
│   └── PaymentOrderResponse.java
├── repository
│   ├── AddressRepository.java
│   ├── BookingRepository.java
│   ├── CategoryRepository.java
│   ├── ComplaintRepository.java
│   ├── CustomerRepository.java
│   ├── NotificationRepository.java
│   ├── PaymentRepository.java
│   ├── ProviderRepository.java
│   ├── ReviewRepository.java
│   ├── ServiceRepository.java
│   ├── SlotRepository.java
│   ├── UserRepository.java
│   └── WalletLedgerRepository.java
├── security
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationEntryPoint.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
│   └── UserPrincipal.java
├── service
│   ├── AdminService.java
│   ├── AuthService.java
│   ├── BookingService.java
│   ├── CategoryCatalogService.java
│   ├── ComplaintService.java
│   ├── NotificationService.java
│   ├── PaymentService.java
│   ├── ProviderService.java
│   └── ReviewService.java
└── util
    ├── GeoUtils.java
    └── SecurityUtils.java
```

---

## 4. Key Business Workflows

### 4.1 Booking Lifecycle State Machine
```
[CUSTOMER INITIATES]
        │
        ▼
   ┌─────────┐
   │ PENDING │
   └────┬────┘
        │
        ├──────────── Provider Rejects ──────────► ┌──────────┐
        │                                          │ REJECTED │
        ├──────────── Customer Cancels ──────────► └──────────┘
        │                                          ┌───────────┐
        ▼                                          │ CANCELLED │
   ┌──────────┐                                    └───────────┘
   │ ACCEPTED │                                          ▲
   └────┬─────┘                                          │
        │ Provider Cancels (Emergency) ──────────────────┘
        ▼
  ┌────────────┐
  │ ON_THE_WAY │
  └─────┬──────┘
        ▼
 ┌─────────────┐
 │ IN_PROGRESS │
 └──────┬──────┘
        ▼
  ┌───────────┐
  │ COMPLETED │ ──► [Trigger: Wallet Credit, Review Enabled]
  └───────────┘
```

### 4.2 Financial Settlement Flow
1. **Quote Calculation**:
   - `Base Amount` = Provider's Custom Price (or Service default base price).
   - `Platform Fee` = 10% of Base Amount.
   - `Tax (GST 18%)` = 18% of Platform Fee.
   - `Final Total` = `Base Amount + Platform Fee + Tax - Discount`.
2. **Customer Checkout**:
   - Order created with gateway (Mock or Razorpay).
   - Customer completes payment via UPI / Card / NetBanking / Cash.
   - Server verifies signature & gateway confirmation -> marks `payments.status = 'SUCCESS'`.
3. **Completion & Provider Payout**:
   - Upon transition to `COMPLETED`, provider's wallet receives `Base Amount` (or `Base Amount - Platform Commission` according to platform terms).
   - Real-time ledger record inserted in `provider_wallet_ledger`.
