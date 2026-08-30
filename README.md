# FixMate – Local Service Booking & Management Platform

🚀 **Live Production Platform**: [https://fixmate-aayush.onrender.com/](https://fixmate-aayush.onrender.com/)  
📦 **GitHub Repository**: [https://github.com/aayushpatidar1511/FixMate](https://github.com/aayushpatidar1511/FixMate)

[![Java](https://img.shields.io/badge/Java-17%20%7C%2021%20%7C%2025-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://www.docker.com/)

> **FixMate** is a production-style, portfolio-ready local services marketplace connecting customers with verified local technicians (electricians, plumbers, AC mechanics, carpenters, cleaners) featuring real-time slot scheduling, server-calculated pricing, double-entry provider ledger, and pluggable payment gateways.

---

## 1. Executive Summary & Features

### For Customers:
- 🔍 **Instant Geo-Search**: Browse professionals filtered by city (**Ujjain, Indore, Bhopal, Dewas**), service category, and spherical distance (Haversine formula).
- 📅 **Dynamic Slot Reservation**: View real-time bookable windows without scheduling collisions.
- 💳 **Transparent Quotes & Pluggable Payments**: Breakdown of base fee, 10% platform commission, and 18% GST with 1-click test payments or live Razorpay.
- 🛵 **Live Status Stepper**: Track jobs from `PENDING` &rarr; `ACCEPTED` &rarr; `ON_THE_WAY` &rarr; `IN_PROGRESS` &rarr; `COMPLETED`.
- ⭐ **Verified Feedback**: Submit 1-to-5 star ratings and reviews upon job completion.
- ⚖️ **Dispute Management**: Raise complaint tickets directly linked to service bookings.

### For Service Providers:
- 📋 **Professional Onboarding**: Profile registration with Aadhaar/Govt ID verification and customizable service pricing.
- ⚡ **Dispatch Desk**: Real-time incoming request notifications with 1-click Accept / Decline.
- ⏰ **Slot Availability Scheduler**: Configure active working hours across Monday to Saturday.
- 💼 **Digital Wallet & Double-Entry Ledger**: Real-time balance accrual upon completed work with transparent transaction history.

### For Administrators:
- 📊 **Executive KPI Dashboard**: Gross GMV, platform commission net revenue, order completion ratios, and dispute metrics.
- 🛡️ **Provider Verification Desk**: Review technician credentials and Approve (`ACTIVE`), Reject (`REJECTED`), or Block accounts.
- ⚖️ **Customer Care Dispute Resolution**: Resolve complaints with recorded admin remarks.

---

## 2. Technology Stack

- **Frontend**: HTML5, Modern CSS3 (Glassmorphism, Dark Mode Tokens, Responsive Grid), Vanilla JavaScript (Modular ES6+).
- **Backend**: Java 17+, Spring Boot 3.3.3, Spring Web, Spring JDBC (`JdbcTemplate`), Spring Validation, Spring Security 6.
- **Database**: MySQL 8.0+ with 17 normalized 3NF relational tables, Triggers, Stored Procedures, Functions, and Views.
- **Security**: Stateless JWT authentication, BCrypt password hashing, Role-Based Access Control (`CUSTOMER`, `PROVIDER`, `ADMIN`).
- **Payment Engine**: Pluggable `PaymentGatewayService` with offline `MockPaymentGatewayService` and Razorpay Test/Live mode.
- **Containerization**: Multi-stage `Dockerfile` and `docker-compose.yml`.

---

## 3. Demo Credentials

All seed accounts use the default password: **`Password@123`**

| Role | Login Identifier | Purpose |
|------|------------------|---------|
| **Customer** | `customer123` (or `customer123@fixmate.in`) | Test booking creation, live tracking & reviews |
| **Provider** | `provider123` (or `provider123@fixmate.in`) | Accept bookings, update job status & view wallet |
| **Admin** | `admin123` (or `admin123@fixmate.in`) | Provider verification, disputes & analytics |

*(Note: The login modal includes 1-click "Quick Demo Fill" buttons for customer123, provider123, and admin123 for instant evaluation!)*

---

## 4. Quick Start & Local Execution

### Option A: Complete Docker Stack (Recommended)
```bash
docker-compose up --build
```
Open **`http://localhost:8080`** in your browser.

### Option B: Native Spring Boot & MySQL

1. **Import Database Scripts**:
   ```bash
   mysql -u root -p < database/01_schema.sql
   mysql -u root -p < database/02_seed.sql
   mysql -u root -p < database/03_indexes.sql
   mysql -u root -p < database/04_views.sql
   mysql -u root -p < database/05_procedures.sql
   mysql -u root -p < database/06_functions.sql
   mysql -u root -p < database/07_triggers.sql
   ```

2. **Run Spring Boot Backend**:
   ```bash
   cd backend
   mvn clean spring-boot:run
   ```

3. **Access Application**:
   Open **`http://localhost:8080`** (Frontend is served directly from Spring Boot static resources).

---

## 5. SQL Developer Portfolio Suite (`database/`)

FixMate includes a complete 9-file enterprise database suite:
- **`01_schema.sql`**: Full DDL for all 17 tables with constraints.
- **`02_seed.sql`**: 20 realistic customers, 20 providers, 10 categories, 30 services, 50+ rate cards, 100+ bookings in Ujjain, Indore, Bhopal, Dewas.
- **`03_indexes.sql`**: Purpose-built composite indexes with `EXPLAIN` query analysis.
- **`04_views.sql`**: Business analytics views (`v_provider_performance_summary`, `v_monthly_financial_analytics`).
- **`05_procedures.sql`**: ACID Stored Procedures for atomic slot reservation and wallet payouts.
- **`06_functions.sql`**: Spherical Haversine distance calculation in pure SQL.
- **`07_triggers.sql`**: Automated rating recalculation and audit history logging.
- **`08_sample_queries.sql`**: Joins (Inner, Left, Right, Self), CTEs, and Transaction demonstration.
- **`09_analytics_queries.sql`**: 20 interview-ready SQL analytics queries using window functions (`ROW_NUMBER`, `DENSE_RANK`, `LAG`, `LEAD`).

---

## 6. Project Documentation Directory

- [System Architecture](docs/architecture.md)
- [Entity-Relationship Diagram](docs/er-diagram.md)
- [Database Data Dictionary](docs/database.md)
- [REST API Documentation](docs/api.md)
- [Deployment & Cloud DevOps](docs/deployment.md)
- [Technical Interview Defense](docs/interview-questions.md)
- [Postman API Collection](postman/FixMate.postman_collection.json)
