# 📚 API Documentation - Capstone2

**Base URL:** `http://localhost:8000`

---

## 🔓 PUBLIC APIs (Không cần Token)

### 1. Đăng ký
```
POST /users
```
**Body:**
```json
{
  "email": "test@gmail.com",
  "password": "123456",
  "fullName": "Nguyen Van A",
  "phone": "0123456789",
  "address": "Da Nang",
  "gender": "male"
}
```
**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Register successfully",
  "data": {
    "userId": "abc123...",
    "email": "test@gmail.com"
  }
}
```

---

### 2. Đăng nhập
```
POST /auth/login
```
**Body:**
```json
{
  "email": "test@gmail.com",
  "password": "123456"
}
```
**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": "abc123...",
    "email": "test@gmail.com",
    "role": "USER"
  }
}
```

---

## 🔐 USER APIs (Cần Token)

**Header:** `Authorization: Bearer <token>`

### 3. Lấy thông tin user của mình
```
GET /users/me
```
**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Get my info successfully",
  "data": {
    "id": "abc123...",
    "email": "test@gmail.com",
    "role": "USER"
  }
}
```

---

### 4. Lấy profile của mình
```
GET /profiles/me
```
**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Get my profile successfully",
  "data": {
    "id": "xyz789...",
    "userId": "abc123...",
    "fullName": "Nguyen Van A",
    "phone": "0123456789",
    "address": "Da Nang",
    "gender": "male"
  }
}
```

---

### 5. Cập nhật profile của mình
```
PUT /profiles/me
```
**Body:** (chỉ gửi field cần update)
```json
{
  "fullName": "Nguyen Van B",
  "phone": "0987654321",
  "address": "Ha Noi",
  "gender": "male"
}
```

---

### 6. Xóa profile của mình
```
DELETE /profiles/me
```

---

### 7. Lấy user theo ID (chỉ xem của mình)
```
GET /users/{id}
```

---

### 8. Cập nhật user theo ID (chỉ sửa của mình)
```
PUT /users/{id}
```
**Body:**
```json
{
  "email": "newemail@gmail.com"
}
```

---

### 9. Xóa user theo ID (chỉ xóa của mình)
```
DELETE /users/{id}
```

---

## 👑 ADMIN APIs (Cần Token + Role ADMIN)

### 10. Lấy danh sách tất cả users
```
GET /users?page=0&size=10
```
**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Get all users successfully",
  "data": {
    "content": [...],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10,
    "first": true,
    "last": false
  }
}
```

---

### 11. Lấy danh sách tất cả profiles
```
GET /profiles?page=0&size=10
```

---

### 12. Lấy profile theo ID
```
GET /profiles/{id}
```

---

### 13. Cập nhật profile theo ID
```
PUT /profiles/{id}
```
**Body:**
```json
{
  "fullName": "New Name",
  "phone": "0123456789"
}
```

---

### 14. Xóa profile theo ID
```
DELETE /profiles/{id}
```

---

### 15. Cập nhật role user (chỉ ADMIN)
```
PUT /users/{id}
```
**Body:**
```json
{
  "role": "ADMIN"
}
```

---

## 📋 Validation Rules

| Field | Rules |
|-------|-------|
| email | Required, Valid email format |
| password | Required, Min 6 characters |
| fullName | Required, 2-100 characters |
| phone | 9-11 digits |
| address | Max 255 characters |
| gender | `male`, `female`, `other` |
| role | `USER`, `ADMIN` |

---

## ❌ Error Codes

| Code | Message | HTTP Status |
|------|---------|-------------|
| 1001 | Email already exists | 400 |
| 1004 | User not found | 404 |
| 1005 | Invalid password | 401 |
| 1006 | Invalid token | 401 |
| 1007 | Profile not found | 404 |
| 1008 | Invalid role | 400 |
| 1009 | You don't have permission | 403 |

---

## 🔑 Roles

| Role | Quyền |
|------|-------|
| USER | Xem/sửa/xóa thông tin của chính mình |
| ADMIN | Xem/sửa/xóa tất cả users và profiles |
