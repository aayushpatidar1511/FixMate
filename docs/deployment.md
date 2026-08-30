# FixMate - Production Deployment & DevOps Guide

## 1. Local Development Deployment

### Prerequisites:
- **Java 17+**
- **MySQL 8.0+**
- Modern Web Browser

### Step 1: Initialize Database
Run the sequential SQL scripts in MySQL CLI or MySQL Workbench:
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

### Step 2: Start Spring Boot Backend
```bash
cd backend
mvn clean spring-boot:run
```
The application will launch on `http://localhost:8080`.

### Step 3: Launch Web Marketplace
- The frontend is embedded in Spring Boot static resources: Simply open `http://localhost:8080/index.html`
- Or run any local static file server:
  ```bash
  cd frontend
  python -m http.server 3000
  ```

---

## 2. Docker & Containerized Orchestration

To boot the complete stack (MySQL + Backend + Static Frontend) with a single command:
```bash
docker-compose up --build
```
This automatically:
1. Pulls and configures official `mysql:8.0` image.
2. Injects the schema, seed records, triggers, functions, and views into `/docker-entrypoint-initdb.d/`.
3. Builds the Spring Boot container via multi-stage Maven build.
4. Binds port `8080` (Web UI & REST APIs) and port `3306` (MySQL).

---

## 3. Production Cloud Deployment (AWS / DigitalOcean / Render)

### Environment Variables Matrix:
```bash
export DB_URL=jdbc:mysql://production-mysql-host:3306/fixmate_db?useSSL=true&requireSSL=true
export DB_USERNAME=fixmate_app
export DB_PASSWORD=SecurePassword2026!
export JWT_SECRET=YourProductionSecretKeyWithMinimum256BitsLengthForHMACSHA256
export PAYMENT_MODE=RAZORPAY
export RAZORPAY_KEY_ID=rzp_live_xxxxxxxx
export RAZORPAY_KEY_SECRET=yyyyyyyyyyyyyyyy
```

### HTTPS & Reverse Proxy (Nginx)
Configure Nginx to terminate SSL and proxy `/api` requests to Spring Boot:
```nginx
server {
    listen 443 ssl http2;
    server_name fixmate.yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/fixmate.yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/fixmate.yourdomain.com/privkey.pem;

    location / {
        root /var/www/fixmate/frontend;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```
