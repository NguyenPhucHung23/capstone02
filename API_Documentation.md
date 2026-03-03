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


## 📦 ORDER APIs (User đã đăng nhập)

**Header:** `Authorization: Bearer <token>`

### 27. Tạo đơn hàng
```
POST /orders
```
**Luồng thanh toán 3 bước:**
1. **Giỏ hàng** - Xem sản phẩm trong giỏ
2. **Thông tin** - Điền/chỉnh sửa thông tin giao hàng (tự động điền từ Profile, có thể sửa)
3. **Thanh toán** - Chọn mã giảm giá + phương thức thanh toán

**Lưu ý:**
- Tạo đơn hàng từ giỏ hàng hiện tại
- Thông tin giao hàng **mặc định lấy từ Profile**, nhưng user **có thể thay đổi** khi đặt hàng
- Sau khi tạo đơn, giỏ hàng sẽ được xóa trống

**Body:**
```json
{
  "customerName": "Nguyễn Văn A",
  "customerPhone": "0123456789",
  "shippingAddress": "123 Đường ABC",
  "shippingCity": "Hồ Chí Minh",
  "shippingDistrict": "Quận 1",
  "shippingWard": "Phường Bến Nghé",
  "paymentMethod": "VNPAY",
  "discountCode": "FREESHIP",
  "note": "Giao giờ hành chính"
}
```

> **Lưu ý:** Các field `customerName`, `customerPhone`, `shippingAddress`, `shippingCity`, `shippingDistrict`, `shippingWard` là **optional**. Nếu không gửi sẽ tự động lấy từ Profile.

**paymentMethod:** `COD`, `VNPAY`, `MOMO`

**discountCode hỗ trợ:**
- `FREESHIP` - Miễn phí vận chuyển (500.000đ)
- `SALE10` - Giảm 10%
- `SALE20` - Giảm 20%
- `NEWYEAR2024` - Giảm 1.000.000đ cho đơn từ 20 triệu

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Đặt hàng thành công",
  "data": {
    "id": "order123...",
    "orderCode": "#ORD-2026-001",
    "customerName": "Nguyễn Văn A",
    "customerEmail": "nguyenvana@gmail.com",
    "customerPhone": "0123456789",
    "fullShippingAddress": "123 Đường ABC, Phường Bến Nghé, Quận 1, Hồ Chí Minh",
    "items": [...],
    "totalItems": 3,
    "subtotal": 25900000,
    "shippingFee": 500000,
    "discount": 500000,
    "discountCode": "FREESHIP",
    "totalAmount": 25900000,
    "paymentMethod": "VNPAY",
    "paymentMethodDisplay": "VNPay",
    "paymentStatus": "PENDING",
    "paymentStatusDisplay": "Chờ thanh toán",
    "status": "PENDING",
    "statusDisplay": "Đang xử lý",
    "createdAt": "2026-03-03T10:00:00Z"
  }
}
```

---

### 28. Xem danh sách đơn hàng của tôi
```
GET /orders?page=0&size=10
```
**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Lấy danh sách đơn hàng thành công",
  "data": {
    "content": [...],
    "page": 0,
    "size": 10,
    "totalElements": 5,
    "totalPages": 1
  }
}
```

---

### 29. Xem chi tiết đơn hàng theo ID
```
GET /orders/{id}
```

---

### 30. Xem chi tiết đơn hàng theo mã đơn
```
GET /orders/code/{orderCode}
```
**Ví dụ:**
```
GET /orders/code/%23ORD-2026-001
```
> Lưu ý: `#` trong URL cần encode thành `%23`

---

### 31. Hủy đơn hàng
```
PUT /orders/{id}/cancel
```
**Lưu ý:** 
- User chỉ hủy được đơn hàng **của mình**
- Chỉ hủy được đơn có trạng thái `PENDING`

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Hủy đơn hàng thành công",
  "data": {
    "orderCode": "#ORD-2026-001",
    "status": "CANCELLED",
    "statusDisplay": "Đã hủy"
  }
}
```

---

## 👑 ADMIN ORDER APIs (Chỉ ADMIN)

### 32. Xem tất cả đơn hàng
```
GET /admin/orders?page=0&size=10
```

---

### 33. Lọc đơn hàng theo trạng thái
```
GET /admin/orders/status/{status}?page=0&size=10
```
**status:** `PENDING`, `CONFIRMED`, `SHIPPING`, `DELIVERED`, `CANCELLED`

**Ví dụ:**
```
GET /admin/orders/status/PENDING
GET /admin/orders/status/SHIPPING
```

---

### 34. Xem chi tiết đơn hàng (Admin)
```
GET /admin/orders/{id}
```

---

### 35. Cập nhật trạng thái đơn hàng (bao gồm Hủy)
```
PUT /admin/orders/{id}/status
```
**Body:**
```json
{
  "status": "SHIPPING"
}
```
**Các trạng thái hợp lệ:**
| Status | Hiển thị |
|--------|----------|
| `PENDING` | Đang xử lý |
| `CONFIRMED` | Đã xác nhận |
| `SHIPPING` | Đang giao |
| `DELIVERED` | Đã giao |
| `CANCELLED` | Đã hủy |

**Lưu ý:**
- Không thể cập nhật đơn hàng đã hủy
- Không thể cập nhật đơn hàng đã giao
- Khi chuyển sang `DELIVERED` và phương thức là `COD`, tự động cập nhật `paymentStatus` thành `PAID`
- **Để hủy đơn:** truyền `status: "CANCELLED"` - chỉ hủy được đơn có trạng thái `PENDING` hoặc `CONFIRMED`

**Response (cập nhật trạng thái):**
```json
{
  "success": true,
  "code": 0,
  "message": "Cập nhật trạng thái đơn hàng thành công",
  "data": {
    "orderCode": "#ORD-2026-001",
    "status": "SHIPPING",
    "statusDisplay": "Đang giao"
  }
}
```

**Response (hủy đơn hàng):**
```json
{
  "success": true,
  "code": 0,
  "message": "Hủy đơn hàng thành công",
  "data": {
    "orderCode": "#ORD-2026-001",
    "status": "CANCELLED",
    "statusDisplay": "Đã hủy"
  }
}
```

---

## ❌ Error Codes (Cập nhật)

| Code | Message | HTTP Status |
|------|---------|-------------|
| 1001 | Email đã tồn tại | 400 |
| 1004 | Không tìm thấy người dùng | 404 |
| 1005 | Mật khẩu không đúng | 401 |
| 1006 | Token không hợp lệ | 401 |
| 1007 | Token đã hết hạn | 401 |
| 1008 | Không tìm thấy hồ sơ | 404 |
| 1009 | Vai trò không hợp lệ | 400 |
| 1010 | Bạn không có quyền truy cập | 403 |
| 1011 | Chưa đăng nhập | 401 |
| 1012 | Không tìm thấy sản phẩm | 404 |
| 1013 | Không tìm thấy giỏ hàng | 404 |
| 1014 | Không tìm thấy sản phẩm trong giỏ hàng | 404 |
| 1015 | Giỏ hàng trống | 400 |
| 1016 | Không tìm thấy đơn hàng | 404 |
| 1017 | Không thể hủy đơn hàng này | 400 |
| 1018 | Đơn hàng đã bị hủy | 400 |
| 1019 | Đơn hàng đã được giao | 400 |
| 1020 | Phương thức thanh toán không hợp lệ | 400 |
| 1021 | Trạng thái đơn hàng không hợp lệ | 400 |

---
