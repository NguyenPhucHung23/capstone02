# 🔓 Auth API

**Base URL:** `http://localhost:8000`

---

## 1. Đăng ký
```
POST /users
```
> Không cần token

**Body:**
```json
{
  "email": "test@gmail.com",
  "password": "Test@123",
  "fullName": "Nguyen Van A",
  "phone": "0912345678",
  "address": "123 Đường Lê Duẩn",
  "city": "Đà Nẵng",
  "ward": "Phường Hải Châu 1",
  "gender": "male",
  "dateOfBirth": "1990-05-15"
}
```

> **Lưu ý địa chỉ:**
> - Dùng `city` nếu là thành phố trực thuộc TW (Hà Nội, Hải Phòng, Huế, Đà Nẵng, Cần Thơ, TPHCM)
> - Dùng `province` nếu là tỉnh (Nghệ An, Thanh Hóa...) — **không dùng cả 2**
> - `ward` là phường/xã thuộc city/province đã chọn

**Validation Rules:**
| Field | Required | Rules |
|-------|----------|-------|
| email | ✅ | Định dạng email hợp lệ |
| password | ✅ | 6-50 ký tự, phải có chữ hoa + chữ thường + số |
| fullName | ✅ | 2-100 ký tự, chỉ chữ cái và khoảng trắng (hỗ trợ tiếng Việt) |
| phone | ✅ | Format VN: `0912345678` hoặc `+84912345678` |
| address | | Max 255 ký tự – số nhà, tên đường... |
| city | | Max 100 ký tự – thành phố trực thuộc TW |
| province | | Max 100 ký tự – tỉnh (dùng `city` **hoặc** `province`) |
| ward | | Max 100 ký tự – phường/xã |
| gender | | `male`, `female`, `other` |
| dateOfBirth | | Ngày trong quá khứ, format: `YYYY-MM-DD` |

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Đăng ký thành công",
  "data": {
    "userId": "abc123...",
    "email": "test@gmail.com"
  }
}
```

---

## 2. Đăng nhập
```
POST /auth/login
```
> Không cần token

**Body:**
```json
{
  "email": "test@gmail.com",
  "password": "Test@123"
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

> Lưu `token` lại để dùng cho các API cần xác thực:  
> `Authorization: Bearer <token>`
