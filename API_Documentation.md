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
  "password": "Test@123",
  "fullName": "Nguyen Van A",
  "phone": "0912345678",
  "address": "123 Đường ABC",
  "city": "Hồ Chí Minh",
  "district": "Quận 1",
  "ward": "Phường Bến Nghé",
  "gender": "male",
  "dateOfBirth": "1990-05-15"
}
```

**Validation Rules:**
| Field | Rules |
|-------|-------|
| email | ✅ Bắt buộc, định dạng email hợp lệ |
| password | ✅ Bắt buộc, 6-50 ký tự, phải có chữ hoa + chữ thường + số |
| fullName | ✅ Bắt buộc, 2-100 ký tự, chỉ chữ cái và khoảng trắng |
| phone | ✅ Bắt buộc, format VN (0912345678 hoặc +84912345678) |
| address | Max 255 ký tự |
| city | Max 100 ký tự |
| district | Max 100 ký tự |
| ward | Max 100 ký tự |
| gender | `male`, `female`, `other` |
| dateOfBirth | Ngày trong quá khứ (format: YYYY-MM-DD) |

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
    "phone": "0912345678",
    "email": "test@gmail.com",
    "address": "123 Đường ABC",
    "city": "Hồ Chí Minh",
    "district": "Quận 1",
    "ward": "Phường Bến Nghé",
    "gender": "male",
    "dateOfBirth": "1990-05-15"
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
  "address": "456 Đường XYZ",
  "city": "Hà Nội",
  "district": "Quận Ba Đình",
  "ward": "Phường Trúc Bạch",
  "gender": "male",
  "dateOfBirth": "1992-08-20"
}
```

**Validation Rules:**
| Field | Rules |
|-------|-------|
| fullName | 2-100 ký tự, chỉ chữ cái và khoảng trắng |
| phone | Format VN (0912345678 hoặc +84912345678) |
| address | Max 255 ký tự |
| city | Max 100 ký tự |
| district | Max 100 ký tự |
| ward | Max 100 ký tự |
| gender | `male`, `female`, `other` |
| dateOfBirth | Ngày trong quá khứ (format: YYYY-MM-DD) |
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
  "phone": "0912345678",
  "address": "New Address",
  "city": "Hồ Chí Minh",
  "district": "Quận 1",
  "ward": "Phường Bến Nghé",
  "gender": "male",
  "dateOfBirth": "1995-03-10"
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

### Đăng ký (RegisterRequest)
| Field | Required | Rules |
|-------|----------|-------|
| email | ✅ | Định dạng email hợp lệ |
| password | ✅ | 6-50 ký tự, phải có: chữ hoa + chữ thường + số |
| fullName | ✅ | 2-100 ký tự, chỉ chữ cái và khoảng trắng (hỗ trợ tiếng Việt) |
| phone | ✅ | Format VN: `0912345678` hoặc `+84912345678` |
| address | | Max 255 ký tự |
| city | | Max 100 ký tự |
| district | | Max 100 ký tự |
| ward | | Max 100 ký tự |
| gender | | `male`, `female`, `other` |
| dateOfBirth | | Ngày trong quá khứ, format: `YYYY-MM-DD` |

### Cập nhật Profile (UpdateProfileRequest)
| Field | Rules |
|-------|-------|
| fullName | 2-100 ký tự, chỉ chữ cái và khoảng trắng |
| phone | Format VN: `0912345678` hoặc `+84912345678` |
| address | Max 255 ký tự |
| city | Max 100 ký tự |
| district | Max 100 ký tự |
| ward | Max 100 ký tự |
| gender | `male`, `female`, `other` |
| dateOfBirth | Ngày trong quá khứ, format: `YYYY-MM-DD` |

### User
| Field | Rules |
|-------|-------|
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

> **Lưu ý:** 
> - Các field `customerName`, `customerPhone`, `shippingAddress`, `shippingCity`, `shippingDistrict`, `shippingWard` là **optional**. Nếu không gửi sẽ tự động lấy từ Profile.
> - Nếu user **chưa có Profile** thì **bắt buộc** phải gửi đầy đủ các thông tin giao hàng trong request.
> - `orderCode` được tự động tạo theo format: `ORD{yyyyMMdd}{timestamp}{random}` để đảm bảo unique.

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
    "orderCode": "ORD20260305123456789",
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
GET /orders/code/ORD202603051234560789
```
> **Lưu ý:** `orderCode` có format `ORD{yyyyMMdd}{nanoTime}{random}` (18 chữ số sau ORD), đảm bảo unique cho mỗi đơn hàng.

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
    "orderCode": "ORD202603051234560789",
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
    "orderCode": "ORD202603051234560789",
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
    "orderCode": "ORD202603059876540321",
    "status": "CANCELLED",
    "statusDisplay": "Đã hủy"
  }
}
```

---

## 💳 VNPAY PAYMENT APIs

### 37. Tạo URL thanh toán VNPay
```
POST /payments/vnpay/create/{orderId}
```
**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `orderId`: ID của đơn hàng cần thanh toán

**Response:**
```json
{
  "code": 200,
  "message": "Tao url thanh toan VNPay thanh cong",
  "data": {
    "orderId": "65f123abc456789def012345",
    "orderCode": "ORDER20260304001",
    "paymentUrl": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=100000000&vnp_Command=pay&vnp_CreateDate=20260304120000&vnp_CurrCode=VND&vnp_ExpireDate=20260304121500&vnp_IpAddr=192.168.1.1&vnp_Locale=vn&vnp_OrderInfo=Thanh+toan+don+hang+ORDER20260304001&vnp_OrderType=other&vnp_ReturnUrl=http%3A%2F%2Flocalhost%3A8000%2Fpayments%2Fvnpay%2Freturn&vnp_TmnCode=CGXZLS0Z&vnp_TxnRef=ORDER20260304001&vnp_Version=2.1.0&vnp_SecureHash=abc123..."
  }
}
```

**Lưu ý:**
- Đơn hàng phải có `paymentMethod = VNPAY`
- Đơn hàng phải có `paymentStatus = PENDING`
- User phải là chủ đơn hàng hoặc admin
- Redirect user đến `paymentUrl` để thanh toán

**Test Cases:**

1. **Test thanh toán thành công:**
   - Tạo order với paymentMethod = "VNPAY"
   - Gọi API tạo payment URL
   - Mở paymentUrl trên browser
   - Sử dụng thẻ test:
     - Số thẻ: `9704198526191432198`
     - Tên: `NGUYEN VAN A`
     - Ngày hết hạn: `07/15`
     - OTP: `123456`
   - Chọn "Thanh toán"
   - Kiểm tra redirect về `/return` với `vnp_ResponseCode=00` và `vnp_TransactionStatus=00`

2. **Test thanh toán thất bại:**
   - Tương tự test 1 nhưng chọn "Hủy thanh toán"
   - Kiểm tra redirect về `/return` với `vnp_ResponseCode=24`

3. **Test order không tồn tại:**
   ```bash
   POST /payments/vnpay/create/invalid_order_id
   # Expected: 404 - Order not found
   ```

4. **Test order không phải VNPAY:**
   ```bash
   # Tạo order với paymentMethod = "COD"
   POST /payments/vnpay/create/{orderId}
   # Expected: 400 - Invalid payment method
   ```

5. **Test order đã thanh toán:**
   ```bash
   # Order có paymentStatus = "PAID"
   POST /payments/vnpay/create/{orderId}
   # Expected: 400 - Order already paid
   ```

---

### 38. VNPay Return Callback (User redirect)
```
GET /payments/vnpay/return
```

**Query Parameters (từ VNPay gửi về):**
- `vnp_TxnRef`: Mã đơn hàng
- `vnp_Amount`: Số tiền (x100)
- `vnp_ResponseCode`: Mã phản hồi (00 = không lỗi)
- `vnp_TransactionStatus`: Mã giao dịch (00 = thành công)
- `vnp_SecureHash`: Chữ ký bảo mật
- ... (các tham số khác)

**Response (Thanh toán thành công):**
```json
{
  "code": 200,
  "message": "VNPay return callback",
  "data": {
    "orderCode": "ORDER20260304001",
    "amount": "100000000",
    "responseCode": "00",
    "transactionStatus": "00",
    "success": "true",
    "message": "Thanh toan thanh cong"
  }
}
```

**Response (Thanh toán thất bại):**
```json
{
  "code": 200,
  "message": "VNPay return callback",
  "data": {
    "orderCode": "ORDER20260304001",
    "amount": "100000000",
    "responseCode": "24",
    "transactionStatus": "02",
    "success": "false",
    "message": "Thanh toan that bai - Ma loi: 24"
  }
}
```

**Lưu ý:**
- Endpoint này **KHÔNG CẬP NHẬT DATABASE**
- Chỉ để hiển thị kết quả cho user
- Frontend nên hiển thị message phù hợp dựa vào `success` field
- Database sẽ được cập nhật bởi IPN callback

**Test Cases:**

1. **Test return với chữ ký hợp lệ:**
   ```bash
   GET /payments/vnpay/return?vnp_TxnRef=ORDER001&vnp_Amount=100000000&vnp_ResponseCode=00&vnp_TransactionStatus=00&vnp_SecureHash=valid_hash...
   # Expected: 200 - success: true
   ```

2. **Test return với chữ ký không hợp lệ:**
   ```bash
   GET /payments/vnpay/return?vnp_TxnRef=ORDER001&vnp_Amount=100000000&vnp_ResponseCode=00&vnp_SecureHash=invalid_hash
   # Expected: 401 - Unauthorized
   ```

3. **Test return với order không tồn tại:**
   ```bash
   GET /payments/vnpay/return?vnp_TxnRef=INVALID_ORDER&vnp_SecureHash=valid_hash...
   # Expected: 404 - Order not found
   ```

4. **Test F5 trang return nhiều lần:**
   ```bash
   # Gọi endpoint return nhiều lần với cùng params
   # Expected: Vẫn trả về kết quả giống nhau (không update DB)
   # Check DB: paymentStatus không thay đổi
   ```

---

### 39. VNPay IPN Callback (Server-to-server)
```
GET /payments/vnpay/ipn
```

**Query Parameters (từ VNPay gọi server-to-server):**
- `vnp_TxnRef`: Mã đơn hàng
- `vnp_Amount`: Số tiền (x100)
- `vnp_ResponseCode`: Mã phản hồi
- `vnp_TransactionStatus`: Mã giao dịch
- `vnp_SecureHash`: Chữ ký bảo mật
- ... (các tham số khác)

**Response (Thành công):**
```json
{
  "RspCode": "00",
  "Message": "Confirm Success"
}
```

**Response (Chữ ký không hợp lệ):**
```json
{
  "RspCode": "97",
  "Message": "Invalid signature"
}
```

**Response (Order không tồn tại):**
```json
{
  "RspCode": "01",
  "Message": "Order not found"
}
```

**Response (Số tiền không khớp):**
```json
{
  "RspCode": "04",
  "Message": "Invalid amount"
}
```

**Response (Order đã thanh toán - Idempotent):**
```json
{
  "RspCode": "00",
  "Message": "Order already confirmed"
}
```

**Lưu ý:**
- Endpoint này **CẬP NHẬT DATABASE**
- VNPay gọi server-to-server (không phải user browser)
- Phải **idempotent** (gọi nhiều lần không ảnh hưởng)
- Chỉ cập nhật khi:
  - `vnp_ResponseCode = "00"` (không lỗi)
  - `vnp_TransactionStatus = "00"` (giao dịch thành công)
  - CẢ HAI phải = "00" thì mới set `paymentStatus = PAID`
- Sử dụng `@Transactional` để đảm bảo atomic

**Test Cases:**

1. **Test IPN với giao dịch thành công:**
   ```bash
   GET /payments/vnpay/ipn?vnp_TxnRef=ORDER001&vnp_Amount=100000000&vnp_ResponseCode=00&vnp_TransactionStatus=00&vnp_SecureHash=valid_hash...
   # Expected: RspCode=00, Message=Confirm Success
   # Check DB: order.paymentStatus = PAID, order.status = CONFIRMED
   ```

2. **Test IPN với giao dịch thất bại:**
   ```bash
   GET /payments/vnpay/ipn?vnp_TxnRef=ORDER001&vnp_Amount=100000000&vnp_ResponseCode=24&vnp_TransactionStatus=02&vnp_SecureHash=valid_hash...
   # Expected: RspCode=00, Message=Confirm Success
   # Check DB: order.paymentStatus = FAILED, order.status = PENDING
   ```

3. **Test IPN với chữ ký không hợp lệ:**
   ```bash
   GET /payments/vnpay/ipn?vnp_TxnRef=ORDER001&vnp_SecureHash=invalid_hash
   # Expected: RspCode=97, Message=Invalid signature
   # Check DB: Không thay đổi
   ```

4. **Test IPN với order không tồn tại:**
   ```bash
   GET /payments/vnpay/ipn?vnp_TxnRef=INVALID&vnp_SecureHash=valid_hash...
   # Expected: RspCode=01, Message=Order not found
   ```

5. **Test IPN với số tiền không khớp:**
   ```bash
   GET /payments/vnpay/ipn?vnp_TxnRef=ORDER001&vnp_Amount=99999999&vnp_SecureHash=valid_hash...
   # Expected: RspCode=04, Message=Invalid amount
   # Check DB: Không thay đổi
   ```

6. **Test IPN idempotent (gọi nhiều lần):**
   ```bash
   # Lần 1: Order chưa thanh toán
   GET /payments/vnpay/ipn?vnp_TxnRef=ORDER001&vnp_Amount=100000000&vnp_ResponseCode=00&vnp_TransactionStatus=00&vnp_SecureHash=valid_hash...
   # Expected: RspCode=00, DB updated
   
   # Lần 2: Order đã thanh toán (gọi lại với cùng params)
   GET /payments/vnpay/ipn?vnp_TxnRef=ORDER001&vnp_Amount=100000000&vnp_ResponseCode=00&vnp_TransactionStatus=00&vnp_SecureHash=valid_hash...
   # Expected: RspCode=00, Message=Order already confirmed
   # Check DB: Không thay đổi (vẫn PAID)
   ```

7. **Test IPN với ResponseCode=00 nhưng TransactionStatus!=00:**
   ```bash
   GET /payments/vnpay/ipn?vnp_TxnRef=ORDER001&vnp_ResponseCode=00&vnp_TransactionStatus=01&vnp_SecureHash=valid_hash...
   # Expected: RspCode=00
   # Check DB: order.paymentStatus = FAILED (vì TransactionStatus != 00)
   ```

---

## 🔍 VNPay Response Codes

### vnp_ResponseCode (Mã phản hồi)
| Code | Mô tả |
|------|-------|
| 00 | Giao dịch thành công |
| 07 | Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường) |
| 09 | Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng |
| 10 | Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần |
| 11 | Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch |
| 12 | Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa |
| 13 | Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP) |
| 24 | Giao dịch không thành công do: Khách hàng hủy giao dịch |
| 51 | Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch |
| 65 | Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày |
| 75 | Ngân hàng thanh toán đang bảo trì |
| 79 | Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định |
| 99 | Các lỗi khác |

### vnp_TransactionStatus (Mã giao dịch)
| Code | Mô tả |
|------|-------|
| 00 | Giao dịch thành công |
| 01 | Giao dịch chưa hoàn tất |
| 02 | Giao dịch bị lỗi |
| 04 | Giao dịch đảo (Khách hàng đã bị trừ tiền tại Ngân hàng nhưng GD chưa thành công ở VNPAY) |
| 05 | VNPAY đang xử lý giao dịch này (GD hoàn tiền) |
| 06 | VNPAY đã gửi yêu cầu hoàn tiền sang Ngân hàng (GD hoàn tiền) |
| 07 | Giao dịch bị nghi ngờ gian lận |
| 09 | GD Hoàn trả bị từ chối |

### IPN RspCode (Response từ merchant về VNPay)
| Code | Mô tả |
|------|-------|
| 00 | Confirm thành công |
| 01 | Order not found |
| 02 | Order already confirmed |
| 04 | Invalid amount |
| 97 | Invalid signature |
| 99 | Unknown error |

---

## 🧪 VNPay Testing Guide

### Môi trường Sandbox
- **URL:** https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
- **TMN Code:** `CGXZLS0Z`
- **Hash Secret:** `XNBCJFAKAZQSGTARRLGCHVZWCIOIGSHN`

### Thẻ test
| Loại | Số thẻ | Tên | Ngày HH | OTP |
|------|--------|-----|---------|-----|
| NCB | 9704198526191432198 | NGUYEN VAN A | 07/15 | 123456 |
| Sacombank | 9704052526191432198 | NGUYEN VAN A | 07/15 | 123456 |
| Vietcombank | 9704062526191432198 | NGUYEN VAN A | 07/15 | 123456 |

### Luồng test đầy đủ

#### 1. Tạo đơn hàng
```bash
POST /orders
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullName": "Nguyen Van A",
  "phone": "0123456789",
  "address": "123 ABC Street, Da Nang",
  "paymentMethod": "VNPAY",
  "discountCode": "",
  "note": "Test VNPAY payment"
}
```

#### 2. Lấy payment URL
```bash
POST /payments/vnpay/create/{orderId}
Authorization: Bearer <token>

# Response:
{
  "data": {
    "paymentUrl": "https://sandbox.vnpayment.vn/..."
  }
}
```

#### 3. Mở payment URL trên browser
- Copy `paymentUrl` và mở trên trình duyệt
- Chọn ngân hàng: NCB
- Nhập thông tin thẻ test
- Nhập OTP: 123456
- Click "Thanh toán"

#### 4. Kiểm tra Return callback
- VNPay sẽ redirect về: `http://localhost:8000/payments/vnpay/return?vnp_...`
- Kiểm tra response JSON có `success: true`
- **Lưu ý:** Ở bước này DB chưa được update

#### 5. Kiểm tra IPN callback (trong logs)
- Xem console logs của server:
  ```
  VNPay IPN callback: {vnp_TxnRef=ORDER001, vnp_ResponseCode=00, ...}
  Order ORDER001 payment SUCCESS
  ```
- Check database:
  ```bash
  GET /orders/{orderId}
  # Expected: paymentStatus = PAID, status = CONFIRMED
  ```

#### 6. Test idempotent
- Gọi lại IPN endpoint với cùng params
- Expected: RspCode=00, DB không thay đổi

### Debug checklist
- [ ] `vnp_SecureHash` được tạo đúng (hashData dùng raw value, không encode)
- [ ] `vnp_Amount` = totalAmount * 100
- [ ] `vnp_ReturnUrl` và `vnp_IpnUrl` accessible từ internet (khi deploy)
- [ ] Check cả `vnp_ResponseCode` VÀ `vnp_TransactionStatus`
- [ ] IPN endpoint phải public (không cần authentication)
- [ ] Return endpoint chỉ hiển thị, IPN endpoint mới update DB
- [ ] Logs đầy đủ để trace vấn đề

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
| 1022 | Vui lòng cung cấp đầy đủ thông tin giao hàng | 400 |

---
