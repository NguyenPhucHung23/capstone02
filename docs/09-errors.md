# ❌ Error Codes

**Base URL:** `http://localhost:8000`

---

## Danh sách mã lỗi

| Code | Message | HTTP Status |
|------|---------|-------------|
| `1001` | Email đã tồn tại | 400 |
| `1002` | Email không hợp lệ | 400 |
| `1003` | Mật khẩu quá ngắn | 400 |
| `1004` | Không tìm thấy người dùng | 404 |
| `1005` | Mật khẩu không đúng | 401 |
| `1006` | Token không hợp lệ | 401 |
| `1007` | Token đã hết hạn | 401 |
| `1008` | Không tìm thấy hồ sơ | 404 |
| `1009` | Vai trò không hợp lệ | 400 |
| `1010` | Bạn không có quyền truy cập | 403 |
| `1011` | Chưa đăng nhập | 401 |
| `1012` | Không tìm thấy sản phẩm | 404 |
| `1013` | Không tìm thấy giỏ hàng | 404 |
| `1014` | Không tìm thấy sản phẩm trong giỏ hàng | 404 |
| `1015` | Giỏ hàng trống | 400 |
| `1016` | Không tìm thấy đơn hàng | 404 |
| `1017` | Không thể hủy đơn hàng này | 400 |
| `1018` | Đơn hàng đã bị hủy | 400 |
| `1019` | Đơn hàng đã được giao | 400 |
| `1020` | Phương thức thanh toán không hợp lệ | 400 |
| `1021` | Trạng thái đơn hàng không hợp lệ | 400 |
| `1022` | Vui lòng cung cấp đầy đủ thông tin giao hàng | 400 |

---

## Format lỗi chuẩn

```json
{
  "success": false,
  "code": 1004,
  "message": "Không tìm thấy người dùng",
  "data": null
}
```

---

## Lỗi Validation (400 Bad Request)

```json
{
  "success": false,
  "code": 400,
  "message": "Validation failed",
  "data": {
    "email": "Định dạng email không hợp lệ",
    "password": "Mật khẩu phải có ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường và số"
  }
}
```
