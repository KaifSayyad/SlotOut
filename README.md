# SlotOut

SlotOut is a multi-tenant SaaS platform that enables small businesses (restaurants, salons, clinics, etc.) to onboard a no-code booking system. Businesses can register, add their services and time slots, and generate a booking link for customers. The system supports JWT-based authentication and full CRUD for tenants, services, time slots, and bookings. OTP verification is used for tenant registration and booking confirmation, but OTP endpoints are not exposed directly; OTP logic is handled internally by the service layer.

---

## Project Flow

1. **Tenant Registration**: Businesses register via `/api/tenants/preRegister` and verify their email with an OTP using `/api/tenants/verifyOtp`.
2. **Login**: Registered tenants log in via `/api/tenants/login` to receive a JWT token.
3. **Service Management**: Authenticated tenants can create, update, and delete services.
4. **Time Slot Management**: Authenticated tenants can create, update, and delete time slots for their services.
5. **Booking**: Customers can view available slots and book a service. Bookings are confirmed via OTP (handled internally).
6. **Swagger URL** : http://localhost:8888/swagger-ui/index.html
---

## API Endpoints

### Tenant Management

#### Register Tenant
- **POST** `/api/tenants/preRegister`
- **Request Example:**
```json
{
  "name": "John's Salon",
  "email": "john@salon.com",
  "phone": "1234567890",
  "address": "123 Main St",
  "password": "password123"
}
```
- **Response Example:**
```json
{
  "tenantId": 1,
  "name": "John's Salon",
  "email": "john@salon.com",
  "message": "OTP sent successfully. Please verify your email."
}
```

#### Verify Tenant OTP (Complete Registration)
- **POST** `/api/tenants/verifyOtp`
- **Request Example:**
```json
{
  "email": "john@salon.com",
  "otp": "123456"
}
```
- **Response Example:**
```json
{
  "message": "OTP Verified successfully!"
}
```

#### Login
- **POST** `/api/tenants/login`
- **Request Example:**
```json
{
  "email": "john@salon.com",
  "password": "password123"
}
```
- **Response Example:**
```json
{
  "id": 1,
  "name": "John's Salon",
  "email": "john@salon.com",
  "token": "<jwt_token>"
}
```

#### Get All Tenants (Admin)
- **GET** `/api/tenants/all`
- **Headers:** `Authorization: Bearer <jwt_token>`
- **Response Example:**
```json
[
  {
    "tenantId": 1,
    "name": "John's Salon",
    "email": "john@salon.com"
  },
  {
    "tenantId": 2,
    "name": "Joye's Salon",
    "email": "joyes@salon.com"
  }
]
```

#### Get Tenant by ID
- **GET** `/api/tenants/{id}`
- **Headers:** `Authorization: Bearer <jwt_token>`
- **Response Example:**
```json
{
  "tenantId": 1,
  "name": "John's Salon",
  "email": "john@salon.com"
}
```

#### Update Tenant
- **PATCH** `/api/tenants/{id}`
- **Headers:** `Authorization: Bearer <jwt_token>`
- **Request Example:**
```json
{
  "name": "John's New Salon",
  "address": "456 New St"
}
```
- **Response Example:**
```json
{
  "tenantId": 1,
  "name": "John's New Salon",
  "email": "john@salon.com",
  "message": "Tenant updated successfully"
}
```

#### Delete Tenant
- **DELETE** `/api/tenants/{id}`
- **Headers:** `Authorization: Bearer <jwt_token>`
- **Response Example:**
```json
{
  "message": "Tenant deleted successfully",
  "error": null
}
```

---

### Service Management

#### Create Service
- **POST** `/api/tenants/{tenantId}/services`
- **Headers:** `Authorization: Bearer <jwt_token>`
- **Request Example:**
```json
{
  "name": "Haircut",
  "description": "Men's Haircut",
  "duration": "00:30:00",
  "price": 25.00
}
```
- **Response Example:**
```json
{
  "serviceId": 1,
  "name": "Haircut",
  "description": "Men's Haircut",
  "isActive": true,
  "message": "Service created successfully"
}
```

#### Update Service
- **PUT** `/api/services/{serviceId}`
- **Headers:** `Authorization: Bearer <jwt_token>`
- **Request Example:**
```json
{
  "name": "Haircut",
  "description": "Updated description",
  "duration": "00:45:00",
  "price": 30.00
}
```
- **Response Example:**
```json
{
  "serviceId": 1,
  "name": "Haircut",
  "description": "Updated description",
  "isActive": true,
  "message": "Service updated successfully"
}
```

#### Delete Service
- **DELETE** `/api/services/{serviceId}`
- **Headers:** `Authorization: Bearer <jwt_token>`
- **Response Example:**
```json
{
  "message": "Service deleted successfully",
  "error": null
}
```

#### Get Services by Tenant
- **GET** `/api/tenants/{tenantId}/services`
- **Headers:** `Authorization: Bearer <jwt_token>`
- **Response Example:**
```json
[
  {
    "serviceId": 1,
    "name": "Haircut",
    "description": "Men's Haircut",
    "isActive": true
  }
]
```

---

### Time Slot Management

#### Create Time Slot
- **POST** `/api/services/{serviceId}/timeslots`
- **Headers:** `Authorization: Bearer <jwt_token>`
- **Request Example:**
```json
{
  "startTime": "2025-05-27T10:00:00",
  "endTime": "2025-05-27T10:30:00"
}
```
- **Response Example:**
```json
{
  "timeSlotId": 1,
  "startTime": "2025-05-27T10:00:00",
  "endTime": "2025-05-27T10:30:00",
  "isBooked": false,
  "message": "Time slot created successfully"
}
```

#### Update Time Slot
- **PUT** `/api/timeslots/{timeSlotId}`
- **Headers:** `Authorization: Bearer <jwt_token>`
- **Request Example:**
```json
{
  "startTime": "2025-05-27T11:00:00",
  "endTime": "2025-05-27T11:30:00"
}
```
- **Response Example:**
```json
{
  "timeSlotId": 1,
  "startTime": "2025-05-27T11:00:00",
  "endTime": "2025-05-27T11:30:00",
  "message": "Time slot updated successfully"
}
```

#### Delete Time Slot
- **DELETE** `/api/timeslots/{timeSlotId}`
- **Headers:** `Authorization: Bearer <jwt_token>`
- **Response Example:**
```json
{
  "message": "Time slot deleted successfully",
  "error": null
}
```

---

### Booking Management

#### Create Booking
- **POST** `/api/bookings`
- **Request Example:**
```json
{
  "tenantId": 1,
  "serviceId": 3,
  "slotId": 12,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890"
}
```
- **Response Example:**
```json
{
  "bookingId": 101,
  "message": "OTP sent to your email. Please verify to confirm booking"
}
```

#### Confirm Booking (with OTP)
- **POST** `/api/bookings/{bookingId}/confirm`
- **Request Example:**
```json
{
  "email": "john@example.com",
  "otp": "123456"
}
```
- **Response Example:**
```json
{
  "bookingId": 101,
  "message": "Booking confirmed successfully"
}
```

#### Get Bookings by Tenant
- **GET** `/api/tenants/{tenantId}/bookings`
- **Headers:** `Authorization: Bearer <jwt_token>`
- **Response Example:**
```json
[
  {
    "bookingId": 101,
    "status": "CONFIRMED"
  }
]
```

#### Cancel Booking
- **DELETE** `/api/bookings/{bookingId}`
- **Headers:** `Authorization: Bearer <jwt_token>`
- **Response Example:**
```json
{
  "message": "Booking cancelled successfully"
}
```

---

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

---

## Security Features

- **Password Security**: Passwords hashed using BCrypt
- **JWT Token Security**: Stateless authentication, 24-hour expiration, includes tenant ID and email claims
- **OTP Verification**: Required for tenant registration and booking confirmation (handled internally)
- **CORS Configuration**: Configured for cross-origin requests, supports all common HTTP methods

---

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
