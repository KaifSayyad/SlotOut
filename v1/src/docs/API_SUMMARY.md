# SlotOut API Documentation

## Overview
SlotOut is a comprehensive slot booking system with JWT-based authentication, OTP verification, and complete CRUD operations for tenants, services, time slots, and bookings.

## Base URL
```
http://localhost:8080
```

## Authentication
- **Public Endpoints**: No authentication required
- **Protected Endpoints**: Require `Authorization: Bearer <jwt_token>` header
- **JWT Token**: Obtained from `/api/tenants/login` endpoint
- **Token Expiry**: 24 hours

## API Endpoints Summary

### 🏢 Tenant Management

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/api/tenants/preRegister` | ❌ | Register new tenant (sends OTP) |
| POST | `/api/tenants/verifyOtp` | ❌ | Verify OTP to complete registration |
| POST | `/api/tenants/login` | ❌ | Login and get JWT token |
| GET | `/api/tenants/` | ✅ | Get all tenants (admin) |
| GET | `/api/tenants/{id}` | ✅ | Get tenant by ID |
| PATCH | `/api/tenants/{id}` | ✅ | Update tenant |
| DELETE | `/api/tenants/{id}` | ✅ | Delete tenant |

### 🔐 OTP Management

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/api/otp/admin/send` | ❌ | Send OTP for admin verification |
| POST | `/api/otp/customer/send` | ❌ | Send OTP for customer verification |
| POST | `/api/otp/verify` | ❌ | Verify OTP |

### 🛎️ Service Management

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/api/tenants/{tenantId}/services` | ✅ | Create new service |
| GET | `/api/tenants/{tenantId}/services` | ✅ | Get all services for tenant |
| PUT | `/api/services/{serviceId}` | ✅ | Update service |
| DELETE | `/api/services/{serviceId}` | ✅ | Delete service |

### ⏰ Time Slot Management

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/api/services/{serviceId}/timeslots` | ✅ | Create new time slot |
| GET | `/api/services/{serviceId}/timeslots/available` | ❌ | Get available time slots (public) |
| PUT | `/api/timeslots/{timeSlotId}` | ✅ | Update time slot |
| DELETE | `/api/timeslots/{timeSlotId}` | ✅ | Delete time slot |

### 📅 Booking Management

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/api/bookings` | ❌ | Create booking (sends OTP) |
| POST | `/api/bookings/{bookingId}/confirm` | ❌ | Confirm booking with OTP |
| GET | `/api/tenants/{tenantId}/bookings` | ✅ | Get bookings for tenant |
| DELETE | `/api/bookings/{bookingId}` | ✅ | Cancel booking |

## Authentication Flow

### 1. Tenant Registration
```
1. POST /api/tenants/preRegister → Sends OTP
2. POST /api/tenants/verifyOtp → Completes registration
3. POST /api/tenants/login → Gets JWT token
```

### 2. Customer Booking
```
1. GET /api/services/{serviceId}/timeslots/available → View slots
2. POST /api/bookings → Create booking (sends OTP)
3. POST /api/bookings/{bookingId}/confirm → Confirm with OTP
```

## Request/Response Examples

### Tenant Registration
```json
// POST /api/tenants/preRegister
{
  "name": "Glow Spa",
  "email": "contact@glowspa.com",
  "phone": "+1234567890",
  "address": "123 Main Street, NY",
  "password": "securePassword123"
}

// Response
"OTP sent successfully!!"
```

### Login Response
```json
// POST /api/tenants/login
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tenantId": "123e4567-e89b-12d3-a456-426614174000",
  "email": "contact@glowspa.com",
  "name": "Glow Spa"
}
```

### Service Creation
```json
// POST /api/tenants/{tenantId}/services
// Authorization: Bearer <token>
{
  "name": "Haircut",
  "description": "Men's Haircut",
  "duration": "00:30:00",  // or "PT30M"
  "price": 25.00
}

// Response
{"message": "Service created successfully"}
```

### Time Slot Creation
```json
// POST /api/services/{serviceId}/timeslots
// Authorization: Bearer <token>
{
  "startTime": "2025-05-27T10:00:00",
  "endTime": "2025-05-27T10:30:00"
}

// Response
{"message": "Time slot created successfully"}
```

### Booking Flow
```json
// Step 1: POST /api/bookings
{
  "tenantId": 1,
  "serviceId": 3,
  "slotId": 12,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890"
}

// Response
{"message": "OTP sent to your email. Please verify to confirm booking"}

// Step 2: POST /api/bookings/{bookingId}/confirm
{
  "email": "john@example.com",
  "otp": "123456"
}

// Response
{"message": "Booking confirmed successfully"}
```

## Error Handling

### Common Error Responses
```json
// Authentication Error
{
  "error": "Email not verified. Please verify your email first",
  "status": 400
}

// Validation Error
{
  "error": "Time slot overlaps with existing slot",
  "status": 400
}

// Not Found Error
{
  "error": "Service not found",
  "status": 400
}

// Server Error
{
  "error": "Internal server error",
  "status": 500
}
```

## Security Features

### 🔐 Password Security
- Passwords hashed using BCrypt
- Minimum requirements enforced in frontend

### 🎫 JWT Token Security
- Stateless authentication
- 24-hour expiration
- Includes tenant ID and email claims

### 📧 OTP Verification
- Required for tenant registration
- Required for booking confirmation
- Redis-based storage with expiration

### 🌐 CORS Configuration
- Configured for cross-origin requests
- Supports all common HTTP methods

## Database Schema

### Key Relationships
```
Tenant (1) ←→ (N) Service
Service (1) ←→ (N) TimeSlot
Tenant (1) ←→ (N) Booking
Service (1) ←→ (N) Booking
TimeSlot (1) ←→ (1) Booking
```

### Key Fields
- **Tenant**: id, name, email, password (hashed), isEmailVerified
- **Service**: id, name, description, duration, price, isActive
- **TimeSlot**: id, startTime, endTime, isBooked
- **Booking**: id, name, email, phone, createdAt

## Deployment Notes

### Required Dependencies
- Spring Boot 3.5.0
- Spring Security
- JWT (JJWT)
- PostgreSQL
- Redis (for OTP storage)
- Spring Mail

### Environment Configuration
- Database connection settings
- Redis connection settings
- SMTP settings for email
- JWT secret key (production)

## Frontend Integration

### Token Storage
```javascript
// Store token after login
localStorage.setItem('authToken', data.token);

// Include in API calls
fetch('/api/protected-endpoint', {
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
    'Content-Type': 'application/json'
  }
});
```

### Error Handling
```javascript
// Handle token expiration
if (response.status === 401) {
  localStorage.removeItem('authToken');
  window.location.href = '/login';
}
```

This API provides a complete slot booking system with robust authentication, OTP verification, and comprehensive CRUD operations suitable for production use.

