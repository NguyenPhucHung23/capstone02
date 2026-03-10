# 🛍️ Cart API

**Base URL:** `http://localhost:8000`  
**Header:** `Authorization: Bearer <token>`

> Mỗi user có 1 giỏ hàng riêng. User chỉ có thể xem/sửa giỏ hàng của chính mình.

---

## 1. Xem giỏ hàng
```
GET /cart
```

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Lấy giỏ hàng thành công",
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

## 2. Thêm sản phẩm vào giỏ
```
POST /cart/items
```

**Lưu ý:**
- Nếu sản phẩm **đã có** trong giỏ → **tăng số lượng**
- Nếu **chưa có** → **thêm mới**

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
  "message": "Thêm vào giỏ hàng thành công",
  "data": {
    "id": "cart123...",
    "items": [...],
    "totalItems": 2,
    "totalPrice": 30000000
  }
}
```

---

## 3. Cập nhật số lượng sản phẩm trong giỏ
```
PUT /cart/items/{productId}
```

**Ví dụ:**
```
PUT /cart/items/prod789...
```

**Lưu ý:** `quantity` phải >= 1

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
  "message": "Cập nhật số lượng thành công",
  "data": {
    "id": "cart123...",
    "items": [...],
    "totalItems": 5,
    "totalPrice": 75000000
  }
}
```

---

## 4. Xóa sản phẩm khỏi giỏ hàng
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
  "message": "Xóa sản phẩm khỏi giỏ hàng thành công",
  "data": {
    "id": "cart123...",
    "items": [...],
    "totalItems": 1,
    "totalPrice": 9500000
  }
}
```
