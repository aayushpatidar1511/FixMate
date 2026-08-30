# FixMate - Spring Boot Backend

## 1. Overview
Production-grade Spring Boot 3 REST API backend for **FixMate – Local Service Booking & Management Platform**.
Built using:
- **Java 17 / 21 / 25**
- **Spring Boot 3.3.3**
- **Spring Security 6 with JJWT**
- **Spring JDBC (NamedParameterJdbcTemplate & JdbcTemplate)**
- **MySQL 8.0+**
- **Dual Payment Engine (Mock Development Gateway + Razorpay Sandbox/Live)**

---

## 2. Configuration & Environment Variables

| Variable | Default Value | Description |
|----------|---------------|-------------|
| `SERVER_PORT` | `8080` | HTTP port for REST APIs |
| `DB_URL` | `jdbc:mysql://localhost:3306/fixmate_db` | MySQL connection string |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `root` | Database password |
| `JWT_SECRET` | *(Secure default key)* | 256-bit HMAC-SHA256 secret for token generation |
| `PAYMENT_MODE` | `MOCK` | `'MOCK'` for local offline testing, `'RAZORPAY'` for live/test Razorpay |
| `RAZORPAY_KEY_ID` | `""` | Razorpay Key ID |
| `RAZORPAY_KEY_SECRET` | `""` | Razorpay Key Secret |

---

## 3. Running Locally

### With Maven:
```bash
mvn clean spring-boot:run
```

### With Docker:
```bash
docker build -t fixmate-backend .
docker run -p 8080:8080 -e DB_URL=jdbc:mysql://host.docker.internal:3306/fixmate_db fixmate-backend
```

---

## 4. API Highlights
- `POST /api/auth/register/customer` - Customer signup
- `POST /api/auth/register/provider` - Provider onboarding
- `POST /api/auth/login` - JWT token issuance
- `GET /api/categories` - Active service taxonomy
- `GET /api/services` - Catalog services with base pricing
- `GET /api/providers/nearby` - Spherical Haversine proximity search
- `POST /api/bookings` - Transactional slot reservation and price calculation
- `POST /api/payments/mock-success` - Instant 1-click test checkout
- `GET /api/admin/dashboard` - Real-time GMV, order volume, and dispute metrics
