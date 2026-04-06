# Feature 3: Wishlist (Danh sách yêu thích)

Cho phép người dùng lưu trữ các sản phẩm yêu thích và xem lại sau.

## API Endpoints

### 1. Lấy danh sách yêu thích
`GET /wishlist`

**Postman Example:**
- **URL:** `{{base_url}}/wishlist`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <user_token>`
- **Response:**
```json
{
  "success": true,
  "data": {
    "id": "wish123",
    "userId": "user456",
    "products": [
      {
        "id": "65f1a...",
        "name": "Bàn Tròn Gỗ Cao Su",
        "price": 2500000,
        "images": ["https://.../img1.jpg"]
      }
    ],
    "totalItems": 1
  }
}
```

### 2. Thêm vào danh sách yêu thích
`POST /wishlist/{productId}`

**Postman Example:**
- **URL:** `{{base_url}}/wishlist/65f1a2b3c4d5e6f7a8b9c0d1`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer <user_token>`

### 3. Xóa khỏi danh sách yêu thích
`DELETE /wishlist/{productId}`

**Postman Example:**
- **URL:** `{{base_url}}/wishlist/65f1a2b3c4d5e6f7a8b9c0d1`
- **Method:** `DELETE`
- **Headers:** `Authorization: Bearer <user_token>`

### 4. Kiểm tra sản phẩm trong wishlist
`GET /wishlist/{productId}/check`

**Postman Example:**
- **URL:** `{{base_url}}/wishlist/65f1a2b3c4d5e6f7a8b9c0d1/check`
- **Method:** `GET`
- **Response:**
```json
{
  "success": true,
  "data": {
    "inWishlist": true
  }
}
```
