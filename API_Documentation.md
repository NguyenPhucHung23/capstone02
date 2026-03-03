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
| 1011 | Product not found | 404 |

---

## 🔑 Roles

| Role | Quyền |
|------|-------|
| USER | Xem/sửa/xóa thông tin của chính mình |
| ADMIN | Xem/sửa/xóa tất cả users và profiles |

---

## 🛒 PRODUCT APIs (User đã đăng nhập)

**Header:** `Authorization: Bearer <token>`

### 16. Lấy danh sách sản phẩm
```
GET /products?page=0&size=10
```
**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Get all products successfully",
  "data": {
    "content": [
      {
        "id": "abc123...",
        "name": "Ghế Sofa Gỗ",
        "slug": "ghe-sofa-go-1234567890",
        "category": "Sofa",
        "price": 15000000,
        "currency": "VND",
        "priceFormatted": "15.000.000 VND",
        "inStock": true,
        "images": ["https://example.com/img1.jpg"],
        ...
      }
    ],
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

### 17. Lấy chi tiết sản phẩm theo ID
```
GET /products/{id}
```

---

## 🛍️ ADMIN PRODUCT APIs (Chỉ ADMIN)

### 18. Tạo/Cập nhật 1 Product
```
POST /admin/products
```
**Lưu ý:**
- Nhận **1 object**
- Nếu `(sourceProvider, sourceUrl)` đã tồn tại → **UPDATE**
- Nếu chưa tồn tại → **CREATE**
- Auto tạo `slug` từ `name`
- Auto set `createdAt`, `updatedAt`

**Body:**
```json
{
  "name": "Ghế Sofa Gỗ",
  "category": "Sofa",
  "price": 15000000,
  "currency": "VND",
  "priceFormatted": "15.000.000 VND",
  "sku": "SF-001",
  "availabilityText": "Còn hàng",
  "inStock": true,
  "stock": null,
  "material": "Gỗ sồi",
  "color": {
    "name": "Nâu",
    "hex": "#8B4513"
  },
  "styles": ["Modern", "Minimalist"],
  "origin": "Vietnam",
  "dimensions": {
    "width": 200,
    "height": 80,
    "depth": 90,
    "unit": "cm"
  },
  "dimensionsRaw": "200x80x90 cm",
  "description": "Ghế sofa cao cấp...",
  "careInstructions": ["Lau bằng khăn ẩm", "Tránh ánh nắng trực tiếp"],
  "notes": ["Bảo hành 12 tháng"],
  "images": ["https://example.com/img1.jpg", "https://example.com/img2.jpg"],
  "sourceUrl": "https://noithat.com/ghe-sofa-go",
  "sourceProvider": "noithat.com"
}
```
**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Product created/updated successfully",
  "data": {
    "id": "abc123...",
    "name": "Ghế Sofa Gỗ",
    "slug": "ghe-sofa-go-1234567890",
    ...
    "createdAt": "2026-03-02T10:00:00Z",
    "updatedAt": "2026-03-02T10:00:00Z"
  }
}
```

---

### 19. Batch Import nhiều Products
```
POST /admin/products/batch
```
**Lưu ý:**
- Nhận **array** các products
- Mỗi product sẽ được tạo mới hoặc update nếu đã tồn tại

**Body:**
```json
[
  {
    "name": "Bàn Trà Gỗ Óc Chó",
    "category": "Bàn",
    "price": 9500000,
    "sourceUrl": "https://noithat.com/ban-tra",
    "sourceProvider": "noithat.com",
    ...
  },
  {
    "name": "Giường Ngủ Gỗ Sồi",
    "sourceUrl": "https://noithat.com/giuong",
    "sourceProvider": "noithat.com",
    ...
  }
]
```
**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Batch import successful: 4 product(s)",
  "data": [...]
}
```

---

### 20. Lấy danh sách Products (Admin)
```
GET /admin/products?page=0&size=10
```

---

### 21. Lấy Product theo ID (Admin)
```
GET /admin/products/{id}
```

---

### 22. Xóa Product
```
DELETE /admin/products/{id}
```

---

## 🛒 CART APIs (User đã đăng nhập)

**Header:** `Authorization: Bearer <token>`

> **Lưu ý:** Mỗi user có 1 giỏ hàng riêng. User chỉ có thể xem/sửa giỏ hàng của chính mình.

### 23. Xem giỏ hàng của tôi
```
GET /cart
```
**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Get cart successfully",
  "data": {
    "id": "cart123...",
    "userId": "user456...",
    "items": [
      {
        "productId": "prod789...",
        "productName": "Ghế Sofa Gỗ",
        "productImage": "https://example.com/img1.jpg",
        "price": 15000000,
        "quantity": 2,
        "subtotal": 30000000
      },
      {
        "productId": "prod012...",
        "productName": "Bàn Trà Gỗ Óc Chó",
        "productImage": "https://example.com/img2.jpg",
        "price": 9500000,
        "quantity": 1,
        "subtotal": 9500000
      }
    ],
    "totalItems": 3,
    "totalPrice": 39500000,
    "createdAt": "2026-03-02T10:00:00Z",
    "updatedAt": "2026-03-02T12:30:00Z"
  }
}
```

---

### 24. Thêm sản phẩm vào giỏ hàng
```
POST /cart/items
```
**Lưu ý:**
- Nếu sản phẩm đã có trong giỏ → **tăng số lượng**
- Nếu chưa có → **thêm mới**

**Body:**
```json
{
  "productId": "prod789...",
  "quantity": 2
}
```
**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Added to cart successfully",
  "data": {
    "id": "cart123...",
    "userId": "user456...",
    "items": [...],
    "totalItems": 2,
    "totalPrice": 30000000,
    ...
  }
}
```

---

### 25. Cập nhật số lượng sản phẩm trong giỏ
```
PUT /cart/items/{productId}
```
**Ví dụ:**
```
PUT /cart/items/prod789...
```
**Lưu ý:**
- Cập nhật số lượng của 1 sản phẩm cụ thể
- Quantity phải >= 1

**Body:**
```json
{
  "quantity": 5
}
```
**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Cart item updated successfully",
  "data": {
    "id": "cart123...",
    "items": [...],
    "totalItems": 5,
    "totalPrice": 75000000,
    ...
  }
}
```

---

### 26. Xóa 1 sản phẩm khỏi giỏ hàng
```
DELETE /cart/items/{productId}
```
**Ví dụ:**
```
DELETE /cart/items/prod789...
```
**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Removed from cart successfully",
  "data": {
    "id": "cart123...",
    "userId": "user456...",
    "items": [
      {
        "productId": "prod012...",
        "productName": "Bàn Trà Gỗ Óc Chó",
        "productImage": "https://example.com/img2.jpg",
        "price": 9500000,
        "quantity": 1,
        "subtotal": 9500000
      }
    ],
    "totalItems": 1,
    "totalPrice": 9500000,
    "createdAt": "2026-03-02T10:00:00Z",
    "updatedAt": "2026-03-03T08:15:00Z"
  }
}
```

---


## ❌ Error Codes (Cập nhật)

| Code | Message | HTTP Status |
|------|---------|-------------|
| 1001 | Email already exists | 400 |
| 1004 | User not found | 404 |
| 1005 | Invalid password | 401 |
| 1006 | Invalid token | 401 |
| 1007 | Expired token | 401 |
| 1008 | Profile not found | 404 |
| 1009 | Invalid role | 400 |
| 1010 | You don't have permission | 403 |
| 1011 | Unauthenticated | 401 |
| 1012 | Product not found | 404 |
| 1013 | Cart not found | 404 |
| 1014 | Cart item not found | 404 |

---
