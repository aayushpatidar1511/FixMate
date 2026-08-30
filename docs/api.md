# FixMate - RESTful API Specification

Base URL: `http://localhost:8080/api`

All JSON responses adhere to the standard envelope format:
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {},
  "timestamp": "2026-08-30T10:00:00"
}
```

---

## 1. Authentication APIs (`/api/auth`)

### 1.1 Customer Registration
- **Endpoint**: `POST /api/auth/register/customer`
- **Access**: Public
- **Request Body**:
```json
{
  "fullName": "Aarav Sharma",
  "email": "aarav@gmail.com",
  "phone": "+919826011001",
  "password": "Password@123"
}
```
- **Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1Ni...",
    "userId": 2,
    "profileId": 1,
    "fullName": "Aarav Sharma",
    "role": "CUSTOMER"
  }
}
```

### 1.2 Provider Registration
- **Endpoint**: `POST /api/auth/register/provider`
- **Access**: Public
- **Request Body**:
```json
{
  "fullName": "Rajesh Sharma",
  "email": "rajesh@fixmate.in",
  "phone": "+919826022001",
  "password": "Password@123",
  "bio": "Govt certified master electrician with 8+ years experience.",
  "experienceYears": 8,
  "city": "Ujjain",
  "state": "Madhya Pradesh",
  "address": "14, Freeganj Main Road",
  "pincode": "456001",
  "latitude": 23.1764720,
  "longitude": 75.7885440,
  "idProofType": "AADHAAR",
  "idProofNumber": "671234981123",
  "serviceIds": [1, 2, 3]
}
```

### 1.3 User Login
- **Endpoint**: `POST /api/auth/login`
- **Request Body**:
```json
{
  "email": "aarav.sharma@gmail.com",
  "password": "Password@123"
}
```

---

## 2. Marketplace & Discovery APIs

### 2.1 Provider Search by Location & Category
- **Endpoint**: `GET /api/providers/search?city=Ujjain&categoryId=1`
- **Access**: Public
- **Response**: Array of `ProviderCardResponse` objects including rating, completed jobs, and service rate cards.

### 2.2 Proximity Search via Spherical Haversine
- **Endpoint**: `GET /api/providers/nearby?lat=23.1765&lon=75.7885&radius=15`
- **Access**: Public
- **Query Params**: `lat`, `lon`, `radius` (in km), `serviceId`, `city`

### 2.3 Slot Availability
- **Endpoint**: `GET /api/providers/{id}/availability?date=2026-09-01`
- **Description**: Returns all unreserved slots for provider on target date.

---

## 3. Booking Engine APIs (`/api/bookings`)

### 3.1 Create Service Booking
- **Endpoint**: `POST /api/bookings`
- **Access**: Authenticated (`CUSTOMER`)
- **Request Body**:
```json
{
  "providerId": 1,
  "serviceId": 1,
  "addressId": 1,
  "bookingDate": "2026-09-01",
  "slotId": 2,
  "problemDescription": "Main breaker tripping continuously"
}
```

### 3.2 Booking Lifecycle Transitions
- `PATCH /api/bookings/{id}/accept` - Provider accepts order (`PENDING` -> `ACCEPTED`)
- `PATCH /api/bookings/{id}/reject` - Provider rejects (`PENDING` -> `REJECTED`)
- `PATCH /api/bookings/{id}/start-travel` - Technician en-route (`ACCEPTED` -> `ON_THE_WAY`)
- `PATCH /api/bookings/{id}/start-service` - Service work started (`ON_THE_WAY` -> `IN_PROGRESS`)
- `PATCH /api/bookings/{id}/complete` - Service completed (`IN_PROGRESS` -> `COMPLETED`, wallet credited)
- `PATCH /api/bookings/{id}/cancel` - Customer or provider cancellation

---

## 4. Payment Gateway APIs (`/api/payments`)

### 4.1 Initiate Order
- **Endpoint**: `POST /api/payments/create-order`
- **Request**: `{ "bookingId": 1 }`

### 4.2 Signature Verification
- **Endpoint**: `POST /api/payments/verify`
- **Request**:
```json
{
  "bookingId": 1,
  "paymentMethod": "UPI",
  "razorpayOrderId": "order_xxx",
  "razorpayPaymentId": "pay_xxx",
  "razorpaySignature": "hex_signature..."
}
```

### 4.3 1-Click Mock Payment (Local Sandbox)
- **Endpoint**: `POST /api/payments/mock-success`
- **Request**: `{ "bookingId": 1, "method": "UPI" }`

---

## 5. Administration APIs (`/api/admin`)
- `GET /api/admin/dashboard` - Real-time GMV, Net Revenue, Orders, Disputes
- `PATCH /api/admin/providers/{id}/verify` - Approve (`ACTIVE`) / Reject (`REJECTED`)
- `PATCH /api/admin/complaints/{id}/status` - Resolve customer disputes
