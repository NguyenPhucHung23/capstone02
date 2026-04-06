# Feature 2: Đánh giá & Nhận xét Sản phẩm

Cho phép người dùng đánh giá và nhận xét các sản phẩm đã mua sau khi đơn hàng được giao.

## API Endpoints

### 1. Tạo đánh giá (User)
`POST /reviews`

**Postman Example:**
- **URL:** `{{base_url}}/reviews`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer <user_token>`
- **Body:**
```json
{
  "productId": "65f1a2b3c4d5e6f7a8b9c0d1",
  "orderCode": "ORD20240320123456",
  "rating": 5,
  "comment": "Sản phẩm rất tuyệt vời, giao hàng nhanh, đóng gói kỹ!"
}
```

### 2. Lấy danh sách đánh giá của sản phẩm (Public)
`GET /reviews/product/{productId}?page=0&size=10`

**Postman Example:**
- **URL:** `{{base_url}}/reviews/product/65f1a2b3c4d5e6f7a8b9c0d1`
- **Method:** `GET`

### 3. Xem tổng hợp đánh giá (Public)
`GET /reviews/product/{productId}/summary`

**Postman Example:**
- **URL:** `{{base_url}}/reviews/product/65f1a2b3c4d5e6f7a8b9c0d1/summary`
- **Method:** `GET`
- **Response:**
```json
{
  "success": true,
  "data": {
    "productId": "65f1a...",
    "avgRating": 4.8,
    "reviewCount": 150,
    "distribution": {
      "1": 2,
      "2": 1,
      "3": 5,
      "4": 42,
      "5": 100
    }
  }
}
```

### 4. Xóa đánh giá (User/Admin)
`DELETE /reviews/{id}`

**Postman Example:**
- **URL:** `{{base_url}}/reviews/65f1a...`
- **Method:** `DELETE`
