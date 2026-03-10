# 🛒 Product API

**Base URL:** `http://localhost:8000`

---

## 🔓 PUBLIC APIs (Không cần token)

### 1. Lấy danh sách sản phẩm
```
GET /products?page=0&size=10
```

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Lấy danh sách sản phẩm thành công",
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
        "images": ["https://example.com/img1.jpg"]
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

### 2. Lấy chi tiết sản phẩm theo ID
```
GET /products/{id}
```

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Lấy sản phẩm thành công",
  "data": {
    "id": "abc123...",
    "name": "Ghế Sofa Gỗ",
    "slug": "ghe-sofa-go-1234567890",
    "category": "Sofa",
    "price": 15000000,
    "currency": "VND",
    "priceFormatted": "15.000.000 VND",
    "sku": "SF-001",
    "availabilityText": "Còn hàng",
    "inStock": true,
    "stock": 10,
    "material": "Gỗ sồi",
    "color": { "name": "Nâu", "hex": "#8B4513" },
    "styles": ["Modern", "Minimalist"],
    "origin": "Vietnam",
    "dimensions": { "width": 200, "height": 80, "depth": 90, "unit": "cm" },
    "dimensionsRaw": "200x80x90 cm",
    "description": "Ghế sofa cao cấp...",
    "careInstructions": ["Lau bằng khăn ẩm"],
    "notes": ["Bảo hành 12 tháng"],
    "images": ["https://example.com/img1.jpg", "https://example.com/img2.jpg"],
    "createdAt": "2026-03-01T10:00:00Z",
    "updatedAt": "2026-03-01T10:00:00Z"
  }
}
```

---

## 👑 ADMIN PRODUCT APIs (Cần token + Role ADMIN)

**Header:** `Authorization: Bearer <admin_token>`

### 3. Tạo hoặc cập nhật 1 sản phẩm
```
POST /admin/products
```

**Lưu ý:**
- Nếu `(sourceProvider, sourceUrl)` đã tồn tại → **UPDATE**
- Nếu chưa tồn tại → **CREATE**
- Auto tạo `slug` từ `name`

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
  "color": { "name": "Nâu", "hex": "#8B4513" },
  "styles": ["Modern", "Minimalist"],
  "origin": "Vietnam",
  "dimensions": { "width": 200, "height": 80, "depth": 90, "unit": "cm" },
  "dimensionsRaw": "200x80x90 cm",
  "description": "Ghế sofa cao cấp...",
  "careInstructions": ["Lau bằng khăn ẩm", "Tránh ánh nắng trực tiếp"],
  "notes": ["Bảo hành 12 tháng"],
  "images": ["https://example.com/img1.jpg"],
  "sourceUrl": "https://noithat.com/ghe-sofa-go",
  "sourceProvider": "noithat.com"
}
```

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Tạo/cập nhật sản phẩm thành công",
  "data": {
    "id": "abc123...",
    "name": "Ghế Sofa Gỗ",
    "slug": "ghe-sofa-go-1234567890",
    "createdAt": "2026-03-02T10:00:00Z",
    "updatedAt": "2026-03-02T10:00:00Z"
  }
}
```

---

### 4. Batch import nhiều sản phẩm
```
POST /admin/products/batch
```

**Lưu ý:** Nhận **array** các products. Mỗi product sẽ được tạo mới hoặc update nếu đã tồn tại.

**Body:**
```json
[
  {
    "name": "Bàn Trà Gỗ Óc Chó",
    "category": "Bàn",
    "price": 9500000,
    "sourceUrl": "https://noithat.com/ban-tra",
    "sourceProvider": "noithat.com"
  },
  {
    "name": "Giường Ngủ Gỗ Sồi",
    "category": "Giường",
    "price": 12000000,
    "sourceUrl": "https://noithat.com/giuong",
    "sourceProvider": "noithat.com"
  }
]
```

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Import thành công: 2 sản phẩm",
  "data": [...]
}
```

---

### 5. Lấy danh sách sản phẩm (Admin – xem đầy đủ thông tin)
```
GET /admin/products?page=0&size=10
```

---

### 6. Lấy sản phẩm theo ID (Admin)
```
GET /admin/products/{id}
```

---

### 7. Xóa sản phẩm
```
DELETE /admin/products/{id}
```

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Xóa sản phẩm thành công",
  "data": null
}
```
