# 📊 Admin Dashboard API

**Base URL:** `http://localhost:8000`  
**Header:** `Authorization: Bearer <admin_token>`

---

## 1. Lấy toàn bộ dashboard (1 request)
```
GET /admin/dashboard?recentOrders=5&topProducts=5
```

| Param | Default | Mô tả |
|-------|---------|-------|
| `recentOrders` | `5` | Số đơn hàng gần đây |
| `topProducts` | `5` | Số sản phẩm bán chạy |

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Lấy dữ liệu dashboard thành công",
  "data": {
    "overview": {
      "totalRevenue": 458500000,
      "totalRevenueFormatted": "458.500.000 ₫",
      "revenueGrowthPercent": 12.5,
      "totalNewOrders": 127,
      "orderGrowthPercent": 8.2,
      "totalProducts": 342,
      "productGrowthPercent": 5.1,
      "totalCustomers": 1253,
      "customerGrowthPercent": 15.3,
      "totalOrders": 890
    },
    "monthlyRevenue": [
      { "year": 2026, "month": 1, "monthLabel": "T1", "revenue": 32000000, "orderCount": 18 },
      { "year": 2026, "month": 2, "monthLabel": "T2", "revenue": 41500000, "orderCount": 24 },
      { "year": 2026, "month": 3, "monthLabel": "T3", "revenue": 58000000, "orderCount": 31 }
    ],
    "recentOrders": [...],
    "bestSellingProducts": [...],
    "orderStatusSummary": {
      "pending": 127,
      "confirmed": 45,
      "shipping": 38,
      "delivered": 652,
      "cancelled": 28
    }
  }
}
```

---

## 2. Lấy 4 thẻ thống kê tổng quan
```
GET /admin/dashboard/overview
```

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Lấy thống kê tổng quan thành công",
  "data": {
    "totalRevenue": 458500000,
    "totalRevenueFormatted": "458.500.000 ₫",
    "revenueGrowthPercent": 12.5,
    "totalNewOrders": 127,
    "orderGrowthPercent": 8.2,
    "totalProducts": 342,
    "productGrowthPercent": 5.1,
    "totalCustomers": 1253,
    "customerGrowthPercent": 15.3,
    "totalOrders": 890
  }
}
```

**Mapping với UI:**
| Field | Thẻ trên UI |
|-------|------------|
| `totalRevenueFormatted` | 💰 Tổng doanh thu |
| `revenueGrowthPercent` | ↑ +12.5% so tháng trước |
| `totalNewOrders` | 🛒 Đơn hàng mới |
| `totalProducts` | 📦 Sản phẩm |
| `totalCustomers` | 👥 Khách hàng |

---

## 3. Doanh thu theo tháng (Biểu đồ)
```
GET /admin/dashboard/revenue/monthly?months=12
```

| Param | Default | Mô tả |
|-------|---------|-------|
| `months` | `12` | Số tháng gần nhất (tối đa 24) |

**Lưu ý:**
- Chỉ tính doanh thu từ đơn có `paymentStatus = PAID`
- Timezone: `Asia/Ho_Chi_Minh (GMT+7)`
- Sắp xếp từ tháng xa → gần nhất

**Response:**
```json
{
  "data": [
    { "year": 2025, "month": 12, "monthLabel": "T12", "revenue": 68000000, "orderCount": 41 },
    { "year": 2026, "month": 1,  "monthLabel": "T1",  "revenue": 32000000, "orderCount": 18 },
    { "year": 2026, "month": 2,  "monthLabel": "T2",  "revenue": 41500000, "orderCount": 24 },
    { "year": 2026, "month": 3,  "monthLabel": "T3",  "revenue": 58000000, "orderCount": 31 }
  ]
}
```

---

## 4. Đơn hàng gần đây
```
GET /admin/dashboard/orders/recent?limit=5
```

| Param | Default | Mô tả |
|-------|---------|-------|
| `limit` | `5` | Số đơn cần lấy |

**Response:**
```json
{
  "data": [
    {
      "id": "order123...",
      "orderCode": "ORD20260309001234",
      "customerName": "Nguyễn Văn A",
      "totalAmount": 25900000,
      "status": "PENDING",
      "statusDisplay": "Đang xử lý",
      "paymentMethod": "VNPAY",
      "paymentMethodDisplay": "VNPay",
      "paymentStatus": "PENDING",
      "createdAt": "2026-03-09T10:00:00Z"
    }
  ]
}
```

---

## 5. Sản phẩm bán chạy nhất
```
GET /admin/dashboard/products/best-selling?limit=5
```

| Param | Default | Mô tả |
|-------|---------|-------|
| `limit` | `5` | Số sản phẩm cần lấy |

**Lưu ý:**
- `soldCount` tăng mỗi khi có đơn hàng mới được tạo
- Sắp xếp theo `soldCount` giảm dần
- `image` là ảnh đầu tiên trong `images` của sản phẩm

**Response:**
```json
{
  "data": [
    {
      "id": "prod789...",
      "name": "Ghế Sofa Modern 3 Chỗ",
      "category": "Sofa",
      "price": 15900000,
      "priceFormatted": "15.900.000 ₫",
      "image": "https://example.com/sofa.jpg",
      "soldCount": 142,
      "stock": 25,
      "inStock": true
    }
  ]
}
```

---

## 6. Tổng kết trạng thái đơn hàng
```
GET /admin/dashboard/orders/status-summary
```

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Lấy tổng kết trạng thái đơn hàng thành công",
  "data": {
    "pending": 127,
    "confirmed": 45,
    "shipping": 38,
    "delivered": 652,
    "cancelled": 28
  }
}
```

---

## 📐 Tóm tắt Endpoints

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/admin/dashboard` | Toàn bộ dashboard (1 request) |
| GET | `/admin/dashboard/overview` | 4 thẻ thống kê tổng quan |
| GET | `/admin/dashboard/revenue/monthly` | Biểu đồ doanh thu theo tháng |
| GET | `/admin/dashboard/orders/recent` | Đơn hàng gần đây |
| GET | `/admin/dashboard/products/best-selling` | Sản phẩm bán chạy nhất |
| GET | `/admin/dashboard/orders/status-summary` | Tổng kết trạng thái đơn |
