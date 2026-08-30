# FixMate - Entity Relationship (ER) Diagram

This document models the relational data architecture of the FixMate platform, highlighting all 17 tables, primary keys, foreign keys, and cardinalities.

```mermaid
erDiagram
    USERS ||--o| CUSTOMERS : "profile for"
    USERS ||--o| SERVICE_PROVIDERS : "profile for"
    USERS ||--o{ BOOKING_STATUS_HISTORY : "changed by"
    USERS ||--o{ NOTIFICATIONS : "receives"

    CUSTOMERS ||--o{ ADDRESSES : "has saved"
    CUSTOMERS ||--o{ BOOKINGS : "places"
    CUSTOMERS ||--o{ REVIEWS : "writes"
    CUSTOMERS ||--o{ COMPLAINTS : "files"

    CATEGORIES ||--o{ SERVICES : "classifies"

    SERVICES ||--o{ PROVIDER_SERVICES : "offered through"
    SERVICES ||--o{ BOOKINGS : "booked in"

    SERVICE_PROVIDERS ||--o{ PROVIDER_SERVICES : "offers"
    SERVICE_PROVIDERS ||--o{ PROVIDER_SLOTS : "schedules"
    SERVICE_PROVIDERS ||--o{ BOOKINGS : "executes"
    SERVICE_PROVIDERS ||--o{ REVIEWS : "reviewed in"
    SERVICE_PROVIDERS ||--o{ PROVIDER_WALLET_LEDGER : "accrues"

    SLOTS ||--o{ PROVIDER_SLOTS : "defines window"
    SLOTS ||--o{ BOOKINGS : "reserved for"

    ADDRESSES ||--o{ BOOKINGS : "delivered at"

    BOOKINGS ||--o{ BOOKING_STATUS_HISTORY : "tracks lifecycle"
    BOOKINGS ||--o{ PAYMENTS : "billed under"
    BOOKINGS ||--o| REVIEWS : "generates rating"
    BOOKINGS ||--o{ COMPLAINTS : "disputed in"
    BOOKINGS ||--o{ REFUNDS : "refunded via"

    PAYMENTS ||--o{ REFUNDS : "reversed through"

    USERS {
        bigint user_id PK
        varchar full_name
        varchar email UK
        varchar phone UK
        varchar password_hash
        enum role
        enum status
        timestamp created_at
        timestamp updated_at
    }

    CUSTOMERS {
        bigint customer_id PK
        bigint user_id FK
        varchar profile_image
        int total_bookings
        timestamp created_at
    }

    SERVICE_PROVIDERS {
        bigint provider_id PK
        bigint user_id FK
        text bio
        int experience_years
        varchar address
        varchar city
        varchar state
        varchar pincode
        decimal latitude
        decimal longitude
        enum verification_status
        varchar id_proof_type
        varchar id_proof_number
        decimal rating_avg
        int rating_count
        int total_completed_jobs
        decimal wallet_balance
        timestamp created_at
        timestamp updated_at
    }

    ADDRESSES {
        bigint address_id PK
        bigint customer_id FK
        varchar label
        varchar street_address
        varchar landmark
        varchar city
        varchar state
        varchar pincode
        decimal latitude
        decimal longitude
        boolean is_default
        timestamp created_at
    }

    CATEGORIES {
        bigint category_id PK
        varchar name UK
        varchar slug UK
        text description
        varchar icon
        boolean is_active
        int display_order
        timestamp created_at
    }

    SERVICES {
        bigint service_id PK
        bigint category_id FK
        varchar service_name
        varchar slug UK
        text description
        decimal base_price
        int duration_minutes
        boolean is_active
        timestamp created_at
    }

    PROVIDER_SERVICES {
        bigint provider_id PK,FK
        bigint service_id PK,FK
        decimal custom_price
        boolean is_available
        timestamp created_at
    }

    SLOTS {
        bigint slot_id PK
        varchar slot_name
        time start_time
        time end_time
    }

    PROVIDER_SLOTS {
        bigint provider_slot_id PK
        bigint provider_id FK
        tinyint day_of_week
        bigint slot_id FK
        boolean is_active
    }

    BOOKINGS {
        bigint booking_id PK
        varchar booking_number UK
        bigint customer_id FK
        bigint provider_id FK
        bigint service_id FK
        bigint address_id FK
        date booking_date
        bigint slot_id FK
        text problem_description
        decimal base_amount
        decimal platform_fee
        decimal tax_amount
        decimal discount_amount
        decimal total_amount
        decimal provider_earnings
        enum booking_status
        enum payment_status
        varchar cancellation_reason
        enum cancelled_by
        timestamp created_at
        timestamp updated_at
    }

    BOOKING_STATUS_HISTORY {
        bigint history_id PK
        bigint booking_id FK
        varchar previous_status
        varchar new_status
        bigint changed_by_user_id FK
        varchar remarks
        timestamp changed_at
    }

    PAYMENTS {
        bigint payment_id PK
        bigint booking_id FK
        varchar transaction_reference UK
        enum payment_method
        enum payment_gateway
        varchar gateway_order_id
        varchar gateway_payment_id
        varchar gateway_signature
        decimal amount
        varchar currency
        enum status
        timestamp paid_at
        timestamp created_at
    }

    REFUNDS {
        bigint refund_id PK
        bigint booking_id FK
        bigint payment_id FK
        decimal amount
        varchar reason
        enum status
        varchar gateway_refund_id
        timestamp processed_at
        timestamp created_at
    }

    REVIEWS {
        bigint review_id PK
        bigint booking_id FK,UK
        bigint customer_id FK
        bigint provider_id FK
        tinyint rating
        text comment
        timestamp created_at
    }

    COMPLAINTS {
        bigint complaint_id PK
        varchar complaint_number UK
        bigint booking_id FK
        bigint customer_id FK
        varchar subject
        text description
        enum status
        text admin_remarks
        timestamp created_at
        timestamp resolved_at
    }

    NOTIFICATIONS {
        bigint notification_id PK
        bigint user_id FK
        varchar title
        text message
        varchar type
        boolean is_read
        bigint reference_id
        timestamp created_at
    }

    PROVIDER_WALLET_LEDGER {
        bigint ledger_id PK
        bigint provider_id FK
        bigint booking_id FK
        enum transaction_type
        decimal amount
        decimal running_balance
        varchar description
        timestamp created_at
    }
```
