# Feature 1: Tìm kiếm & Lọc Sản phẩm

Cho phép người dùng và quản trị viên tìm kiếm sản phẩm theo nhiều tiêu chí khác nhau.

## API Endpoints

### 1. Tìm kiếm sản phẩm (Public)
`GET /products/search`

| Parameter | Mô tả | Ví dụ |
|-----------|-------|-------|
| `query` | Từ khóa tìm kiếm (theo tên) | `sofa` |
| `category` | Danh mục sản phẩm | `Sofa` |
| `minPrice` | Giá tối thiểu | `5000000` |
| `maxPrice` | Giá tối đa | `20000000` |
| `inStock` | Còn hàng hay không | `true` |
| `sortBy` | Sắp xếp theo (`price`, `name`, `soldCount`, `createdAt`) | `price` |
| `sortDir` | Thứ tự (`asc`, `desc`) | `asc` |

**Postman Example:**
- **URL:** `{{base_url}}/products/search?query=sofa&minPrice=10000000&sortBy=price&sortDir=asc`
- **Method:** `GET`
- **Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Tìm kiếm sản phẩm thành công",
  "data": {
    "content": [
      {
        "id": "65f1a...",
        "name": "Sofa Hiện Đại",
        "price": 12000000,
        "avgRating": 4.5,
        "reviewCount": 12
      }
    ],
    "totalPages": 1,
    "totalElements": 1
  }
}
```

### 2. Tìm kiếm sản phẩm (Admin)
`GET /admin/products/search`

Tương tự như public endpoint nhưng trả về đầy đủ thông tin sản phẩm (bao gồm `soldCount`, `sourceUrl`, ...).

**Postman Example:**
- **URL:** `{{base_url}}/admin/products/search?query=ghe&inStock=false`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <admin_token>`
