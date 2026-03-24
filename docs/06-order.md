# 📦 Order API

**Base URL:** `http://localhost:8000`  
**Header:** `Authorization: Bearer <token>`

---

## 🔐 USER APIs

### 1. Tạo đơn hàng
```
POST /orders
```

**Luồng đặt hàng 3 bước:**
1. **Giỏ hàng** – Xem sản phẩm trong giỏ
2. **Thông tin** – Điền/chỉnh sửa thông tin giao hàng (tự động điền từ Profile)
3. **Thanh toán** – Chọn mã giảm giá + phương thức thanh toán

**Lưu ý:**
- Tạo đơn hàng từ **giỏ hàng hiện tại**
- Thông tin giao hàng mặc định lấy từ **Profile**, user có thể thay đổi
- Sau khi tạo đơn, giỏ hàng sẽ **tự động xóa trống**
- `orderCode` được tạo tự động theo format: `ORD{yyyyMMdd}{timestamp}{random}`

**Body:**
```json
{
  "customerName": "Nguyễn Văn A",
  "customerPhone": "0123456789",
  "shippingAddress": "123 Đường Lê Duẩn",
  "shippingCity": "Đà Nẵng",
  "shippingWard": "Phường Hải Châu 1",
  "paymentMethod": "VNPAY",
  "discountCode": "FREESHIP",
  "note": "Giao giờ hành chính"
}
```

> **Lưu ý fields:**
> - `customerName`, `customerPhone`, `shippingAddress`, `shippingCity`/`shippingProvince`, `shippingWard` là **optional** – nếu không gửi sẽ tự lấy từ Profile
> - Nếu **chưa có Profile** thì **bắt buộc** gửi đầy đủ thông tin giao hàng
> - Dùng `shippingCity` **hoặc** `shippingProvince`, không dùng cả 2

**Ví dụ dùng tỉnh:**
```json
{
  "shippingProvince": "Nghệ An",
  "shippingWard": "Xã Nghi Phú",
  "shippingAddress": "Xóm 5, đường Nguyễn Văn Cừ",
  "paymentMethod": "COD"
}
```

**paymentMethod hợp lệ:** `COD`, `VNPAY`, `MOMO`

**discountCode hỗ trợ:**
| Code | Mô tả |
|------|-------|
| `FREESHIP` | Miễn phí vận chuyển (giảm 500.000đ) |
| `SALE10` | Giảm 10% |
| `SALE20` | Giảm 20% |
| `NEWYEAR2024` | Giảm 1.000.000đ cho đơn từ 20 triệu |

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Tạo đơn hàng thành công",
  "data": {
    "id": "order123...",
    "orderCode": "ORD20260305123456789",
    "customerName": "Nguyễn Văn A",
    "customerEmail": "nguyenvana@gmail.com",
    "customerPhone": "0123456789",
    "fullShippingAddress": "123 Đường Lê Duẩn, Phường Hải Châu 1, Đà Nẵng",
    "items": [
      {
        "productId": "prod789...",
        "productName": "Ghế Sofa Gỗ",
        "price": 15000000,
        "quantity": 2,
        "subtotal": 30000000
      }
    ],
    "totalItems": 2,
    "subtotal": 30000000,
    "shippingFee": 500000,
    "discount": 500000,
    "discountCode": "FREESHIP",
    "totalAmount": 30000000,
    "paymentMethod": "VNPAY",
    "paymentMethodDisplay": "VNPay",
    "paymentStatus": "PENDING",
    "paymentStatusDisplay": "Chờ thanh toán",
    "status": "PENDING",
    "statusDisplay": "Đang xử lý",
    "createdAt": "2026-03-05T10:00:00Z"
  }
}
```

---

### 2. Xem danh sách đơn hàng của mình
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

### 3. Xem chi tiết đơn hàng theo ID
```
GET /orders/{id}
```

---

### 4. Xem chi tiết đơn hàng theo mã đơn
```
GET /orders/code/{orderCode}
```

**Ví dụ:**
```
GET /orders/code/ORD20260305123456789
```

---

### 5. Hủy đơn hàng
```
PUT /orders/{id}/cancel
```

**Lưu ý:**
- User chỉ hủy được đơn **của mình**
- Chỉ hủy được đơn có trạng thái `PENDING`

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Hủy đơn hàng thành công",
  "data": {
    "orderCode": "ORD20260305123456789",
    "status": "CANCELLED",
    "statusDisplay": "Đã hủy"
  }
}
```

---

## 👑 ADMIN ORDER APIs (Cần token + Role ADMIN)

### 6. Xem tất cả đơn hàng
```
GET /admin/orders?page=0&size=10
```

---

### 7. Lọc đơn hàng theo trạng thái
```
GET /admin/orders/status/{status}?page=0&size=10
```

**Ví dụ:**
```
GET /admin/orders/status/PENDING
GET /admin/orders/status/SHIPPING
```

**status hợp lệ:** `PENDING`, `CONFIRMED`, `SHIPPING`, `DELIVERED`, `CANCELLED`

---

### 8. Xem chi tiết đơn hàng (Admin)
```
GET /admin/orders/{id}
```

---

### 9. Cập nhật trạng thái đơn hàng
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

**Quy tắc:**
- Không thể cập nhật đơn đã hủy hoặc đã giao
- Khi chuyển sang `DELIVERED` với `COD` → tự động set `paymentStatus = PAID`
- Để hủy đơn: truyền `status: "CANCELLED"` – chỉ hủy được đơn `PENDING` hoặc `CONFIRMED`

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Cập nhật trạng thái đơn hàng thành công",
  "data": {
    "orderCode": "ORD20260305123456789",
    "status": "SHIPPING",
    "statusDisplay": "Đang giao"
  }
}

---

### 10. Tìm kiếm & Lọc đơn hàng nâng cao (Admin)
`GET /admin/orders/search`

Cung cấp cho quản trị viên công cụ mạnh mẽ để tìm kiếm và theo dõi các đơn hàng.

**Các tiêu chí lọc (Query Parameters):**

| Parameter | Mô tả | Ví dụ |
|-----------|-------|-------|
| `keyword` | Tìm theo mã đơn, tên khách, email (đa năng) | `Nguyễn Văn A` |
| `orderCode` | Lọc theo mã đơn cụ thể | `ORD20240320...` |
| `customerName` | Lọc theo tên khách hàng | `Nguyễn Văn A` |
| `customerEmail` | Lọc theo email khách hàng | `user@example.com` |
| `status` | Trạng thái đơn hàng | `PENDING` |
| `paymentMethod`| Phương thức thanh toán | `COD` |
| `paymentStatus`| Trạng thái thanh toán | `PAID` |
| `fromDate` | Từ ngày (yyyy-MM-dd) | `2024-03-01` |
| `toDate` | Đến ngày (yyyy-MM-dd) | `2024-03-31` |

**Postman Examples:**

- **Tìm theo trạng thái & thanh toán:** `{{base_url}}/admin/orders/search?status=PENDING&paymentMethod=COD`
- **Tìm theo mã đơn:** `{{base_url}}/admin/orders/search?orderCode=ORD20240320`
- **Tìm theo tên khách:** `{{base_url}}/admin/orders/search?customerName=Nguyen Van A`
- **Tìm theo khoảng thời gian:** `{{base_url}}/admin/orders/search?fromDate=2024-03-01&toDate=2024-03-31`
```
