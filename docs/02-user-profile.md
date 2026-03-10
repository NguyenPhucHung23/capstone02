# 👤 User & Profile API

**Base URL:** `http://localhost:8000`  
**Auth Header:** `Authorization: Bearer <token>`

---

## 🔐 USER APIs (Cần token)

### 1. Lấy thông tin user của mình
```
GET /users/me
```

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Lấy thông tin thành công",
  "data": {
    "id": "abc123...",
    "email": "test@gmail.com",
    "role": "USER"
  }
}
```

---

### 2. Lấy user theo ID (chỉ xem của mình)
```
GET /users/{id}
```

---

### 3. Cập nhật user theo ID (chỉ sửa của mình)
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

### 4. Xóa user theo ID (chỉ xóa của mình)
```
DELETE /users/{id}
```

---

### 5. Lấy profile của mình
```
GET /profiles/me
```

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Lấy hồ sơ thành công",
  "data": {
    "id": "xyz789...",
    "userId": "abc123...",
    "fullName": "Nguyen Van A",
    "phone": "0912345678",
    "email": "test@gmail.com",
    "address": "123 Đường Lê Duẩn",
    "city": "Đà Nẵng",
    "province": null,
    "ward": "Phường Hải Châu 1",
    "gender": "male",
    "dateOfBirth": "1990-05-15"
  }
}
```

> `city` và `province` chỉ có 1 giá trị, cái còn lại là `null`.

---

### 6. Cập nhật profile của mình
```
PUT /profiles/me
```

**Body:** (chỉ gửi field cần update)
```json
{
  "fullName": "Nguyen Van B",
  "phone": "0987654321",
  "address": "456 Đường ABC",
  "city": "Hà Nội",
  "ward": "Phường Hoàn Kiếm",
  "gender": "male",
  "dateOfBirth": "1992-08-20"
}
```

> **Ví dụ dùng tỉnh:**
> ```json
> {
>   "province": "Nghệ An",
>   "ward": "Xã Nghi Phú",
>   "address": "Xóm 5, đường Nguyễn Văn Cừ"
> }
> ```

**Validation Rules:**
| Field | Rules |
|-------|-------|
| fullName | 2-100 ký tự, chỉ chữ cái và khoảng trắng |
| phone | Format VN: `0912345678` hoặc `+84912345678` |
| address | Max 255 ký tự |
| city | Max 100 ký tự – thành phố TW (dùng `city` **hoặc** `province`) |
| province | Max 100 ký tự – tỉnh (dùng `city` **hoặc** `province`) |
| ward | Max 100 ký tự – phường/xã |
| gender | `male`, `female`, `other` |
| dateOfBirth | Ngày trong quá khứ, format: `YYYY-MM-DD` |

---

### 7. Xóa profile của mình
```
DELETE /profiles/me
```

---

## 👑 ADMIN APIs (Cần token + Role ADMIN)

### 8. Lấy danh sách tất cả users
```
GET /users?page=0&size=10
```

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Lấy danh sách người dùng thành công",
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

### 9. Cập nhật role user (chỉ ADMIN)
```
PUT /users/{id}
```

**Body:**
```json
{
  "role": "ADMIN"
}
```

| Giá trị `role` hợp lệ |
|----------------------|
| `USER` |
| `ADMIN` |

---

### 10. Lấy danh sách tất cả profiles
```
GET /profiles?page=0&size=10
```

---

### 11. Lấy profile theo ID
```
GET /profiles/{id}
```

---

### 12. Cập nhật profile theo ID
```
PUT /profiles/{id}
```

**Body:**
```json
{
  "fullName": "New Name",
  "phone": "0912345678",
  "address": "New Address",
  "city": "Hồ Chí Minh",
  "ward": "Phường Bến Nghé",
  "gender": "male",
  "dateOfBirth": "1995-03-10"
}
```

---

### 13. Xóa profile theo ID
```
DELETE /profiles/{id}
```
