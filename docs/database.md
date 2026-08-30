# FixMate - Database Architecture & SQL Engineering Documentation

## 1. Relational Database Design Overview

The FixMate database schema (`fixmate_db`) is designed adhering to **Third Normal Form (3NF)** standards to eliminate update anomalies, reduce redundancy, and preserve strict referential integrity across all transactional entities.

### Key Database Metrics:
- **Tables**: 17 Relational Tables
- **Storage Engine**: InnoDB (ACID compliant, row-level locking, foreign key support)
- **Character Set**: `utf8mb4` with `utf8mb4_unicode_ci` (Full Unicode & multilingual character support)
- **Financial Precision**: All monetary fields (`base_amount`, `platform_fee`, `tax_amount`, `total_amount`, `wallet_balance`) use `DECIMAL(10, 2)` or `DECIMAL(12, 2)` to eliminate IEEE floating-point arithmetic errors.

---

## 2. Table Catalog & Data Dictionary

| # | Table Name | Purpose | Primary Key | Foreign Keys | Key Constraints |
|---|------------|---------|-------------|--------------|-----------------|
| 1 | `users` | Base identity & credentials | `user_id` | - | `UNIQUE(email)`, `UNIQUE(phone)` |
| 2 | `customers` | Customer profiles & metrics | `customer_id` | `user_id` -> `users` | `UNIQUE(user_id)` |
| 3 | `service_providers` | Technician profile, ratings & GPS | `provider_id` | `user_id` -> `users` | `UNIQUE(user_id)`, `INDEX(city)` |
| 4 | `addresses` | Customer saved locations | `address_id` | `customer_id` -> `customers` | `is_default` flag |
| 5 | `categories` | Service taxonomies | `category_id` | - | `UNIQUE(name)`, `UNIQUE(slug)` |
| 6 | `services` | Specific marketplace services | `service_id` | `category_id` -> `categories` | `UNIQUE(slug)` |
| 7 | `provider_services` | M:N provider pricing catalog | `(provider_id, service_id)` | `provider_id`, `service_id` | Composite PK |
| 8 | `slots` | Canonical service time windows | `slot_id` | - | `CHECK(start_time < end_time)` |
| 9 | `provider_slots` | Weekly working day slot schedules | `provider_slot_id` | `provider_id`, `slot_id` | `UNIQUE(provider_id, day, slot)` |
| 10 | `bookings` | Core service order entity | `booking_id` | `customer_id`, `provider_id`, `service_id`, `slot_id` | `UNIQUE(booking_number)` |
| 11 | `booking_status_history` | Complete lifecycle audit log | `history_id` | `booking_id`, `changed_by_user_id` | Timestamped transitions |
| 12 | `payments` | Customer transactions & gateway IDs | `payment_id` | `booking_id` | `UNIQUE(transaction_reference)` |
| 13 | `refunds` | Reversals on cancelled orders | `refund_id` | `booking_id`, `payment_id` | Status machine |
| 14 | `reviews` | Post-completion customer ratings | `review_id` | `booking_id`, `customer_id`, `provider_id` | `UNIQUE(booking_id)`, `CHECK(1..5)` |
| 15 | `complaints` | Customer dispute resolution | `complaint_id` | `booking_id`, `customer_id` | `UNIQUE(complaint_number)` |
| 16 | `notifications` | In-app user notifications | `notification_id` | `user_id` | `is_read` indexed |
| 17 | `provider_wallet_ledger` | Double-entry provider accounting | `ledger_id` | `provider_id`, `booking_id` | Immutable audit trail |

---

## 3. Database Automation (Triggers, Procedures & Functions)

### 3.1 Triggers
- **`trg_after_review_insert` & `trg_after_review_update`**: Automatically updates `service_providers.rating_avg` and `rating_count` without requiring manual batch recalculation or stale cache queries.
- **`trg_before_booking_insert_guard`**: Rejects any booking if the requested date is in the past, or if a non-cancelled booking already exists for that provider, date, and slot.
- **`trg_after_booking_status_audit`**: Automatically inserts a row into `booking_status_history` whenever `booking_status` changes, providing audit protection against rogue application updates.

### 3.2 Stored Procedures
- **`sp_create_booking`**: Implements row-level locking (`SELECT ... FOR UPDATE`), server-side price calculation, booking record insertion, audit logging, and provider notification inside a single transaction.
- **`sp_complete_booking_payout`**: Marks booking `COMPLETED`, adds net earnings to the technician's wallet, writes the ledger entry, and prompts customer review.
- **`sp_cancel_booking_and_refund`**: Updates status to `CANCELLED`, restores slot availability, and queues refund if prepaid.

### 3.3 Functions
- **`fn_haversine_distance_km(lat1, lon1, lat2, lon2)`**: Calculates distance between customer and provider using the Haversine formula in pure SQL.
- **`fn_calculate_commission(base_amount, percentage)`**: Standardized commission calculation.
- **`fn_get_provider_completion_rate(provider_id)`**: Calculates percentage of fulfilled vs cancelled jobs.

---

## 4. Execution Sequence

To execute the database scripts against a MySQL instance:

```bash
mysql -u root -p < database/01_schema.sql
mysql -u root -p < database/02_seed.sql
mysql -u root -p < database/03_indexes.sql
mysql -u root -p < database/04_views.sql
mysql -u root -p < database/05_procedures.sql
mysql -u root -p < database/06_functions.sql
mysql -u root -p < database/07_triggers.sql
mysql -u root -p < database/08_sample_queries.sql
mysql -u root -p < database/09_analytics_queries.sql
```
