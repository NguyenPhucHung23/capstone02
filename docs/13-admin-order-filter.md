# Feature 4: Lọc Đơn hàng nâng cao (Admin)

Cung cấp cho quản trị viên công cụ mạnh mẽ để tìm kiếm và theo dõi các đơn hàng.

## API Endpoint
`GET /admin/orders/search`

## Các tiêu chí lọc (Query Parameters)

| Parameter | Mô tả | Ví dụ |
|-----------|-------|-------|
| `keyword` | Tìm theo mã đơn, tên khách, email | `ORD20240320` |
| `status` | Trạng thái đơn hàng | `PENDING` |
| `paymentMethod`| Phương thức thanh toán | `COD` |
| `paymentStatus`| Trạng thái thanh toán | `PAID` |
| `fromDate` | Từ ngày (yyyy-MM-dd) | `2024-03-01` |
| `toDate` | Đến ngày (yyyy-MM-dd) | `2024-03-31` |

## Postman Example

### 1. Tìm đơn hàng COD đang chờ xử lý
- **URL:** `{{base_url}}/admin/orders/search?status=PENDING&paymentMethod=COD`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <admin_token>`

### 2. Tìm đơn hàng theo mã đơn cụ thể
- **URL:** `{{base_url}}/admin/orders/search?keyword=ORD20240320123456`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <admin_token>`

### 3. Tìm đơn hàng trong khoảng thời gian
- **URL:** `{{base_url}}/admin/orders/search?fromDate=2024-03-01&toDate=2024-03-31`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <admin_token>`

## Response Structure
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "ord_1",
        "orderCode": "ORD2024...",
        "customerName": "Nguyễn Văn A",
        "totalAmount": 15000000,
        "status": "PENDING",
        "createdAt": "2024-03-20T..."
      }
    ],
    "totalPages": 5,
    "totalElements": 50
  }
}
```
