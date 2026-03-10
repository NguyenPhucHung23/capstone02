# 💳 VNPay Payment API

**Base URL:** `http://localhost:8000`

---

## 1. Tạo URL thanh toán VNPay
```
POST /payments/vnpay/create/{orderId}
```

**Header:** `Authorization: Bearer <token>`

**Lưu ý:**
- Đơn hàng phải có `paymentMethod = VNPAY`
- Đơn hàng phải có `paymentStatus = PENDING`
- User phải là chủ đơn hàng hoặc ADMIN
- Redirect user đến `paymentUrl` để thanh toán

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Tao url thanh toan VNPay thanh cong",
  "data": {
    "orderId": "65f123abc456789def012345",
    "orderCode": "ORD20260305123456789",
    "paymentUrl": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=..."
  }
}
```

---

## 2. VNPay Return Callback (User redirect)
```
GET /payments/vnpay/return
```

> Query params do VNPay tự gửi về, không cần gửi thủ công.

**Lưu ý:**
- Trên **localhost**: endpoint này cũng **cập nhật DB** (vì IPN không gọi được từ sandbox)
- Trên **production**: chỉ hiển thị kết quả, DB được update bởi IPN

**Response (Thành công):**
```json
{
  "success": true,
  "code": 0,
  "message": "VNPay return callback",
  "data": {
    "orderCode": "ORD20260305123456789",
    "amount": "2590000000",
    "responseCode": "00",
    "transactionStatus": "00",
    "success": "true",
    "message": "Thanh toan thanh cong"
  }
}
```

**Response (Thất bại / Hủy):**
```json
{
  "success": true,
  "code": 0,
  "message": "VNPay return callback",
  "data": {
    "orderCode": "ORD20260305123456789",
    "responseCode": "24",
    "transactionStatus": "02",
    "success": "false",
    "message": "Thanh toan that bai - Ma loi: 24"
  }
}
```

---

## 3. VNPay IPN Callback (Server-to-server)
```
GET /payments/vnpay/ipn
```

> VNPay tự gọi server-to-server. Endpoint này **cập nhật DB**.

**Quy tắc cập nhật:**
- Chỉ cập nhật khi `vnp_ResponseCode = "00"` **VÀ** `vnp_TransactionStatus = "00"`
- Phải **idempotent** – gọi nhiều lần không ảnh hưởng

**Response (Thành công):**
```json
{ "RspCode": "00", "Message": "Confirm Success" }
```

**Response (Chữ ký không hợp lệ):**
```json
{ "RspCode": "97", "Message": "Invalid signature" }
```

**Response (Order không tồn tại):**
```json
{ "RspCode": "01", "Message": "Order not found" }
```

**Response (Số tiền không khớp):**
```json
{ "RspCode": "04", "Message": "Invalid amount" }
```

**Response (Đã thanh toán – idempotent):**
```json
{ "RspCode": "00", "Message": "Order already confirmed" }
```

---

## 🧪 Testing Guide

### Môi trường Sandbox
| | |
|-|-|
| **URL** | https://sandbox.vnpayment.vn/paymentv2/vpcpay.html |
| **TMN Code** | `CGXZLS0Z` |
| **Hash Secret** | `XNBCJFAKAZQSGTARRLGCHVZWCIOIGSHN` |

### Thẻ test
| Ngân hàng | Số thẻ | Tên | Ngày HH | OTP |
|-----------|--------|-----|---------|-----|
| NCB | `9704198526191432198` | NGUYEN VAN A | 07/15 | 123456 |
| Sacombank | `9704052526191432198` | NGUYEN VAN A | 07/15 | 123456 |
| Vietcombank | `9704062526191432198` | NGUYEN VAN A | 07/15 | 123456 |

### Luồng test đầy đủ
1. Tạo đơn hàng: `POST /orders` với `"paymentMethod": "VNPAY"`
2. Lấy payment URL: `POST /payments/vnpay/create/{orderId}`
3. Mở `paymentUrl` trên browser → chọn NCB → nhập thẻ test → OTP: `123456`
4. VNPay redirect về `/payments/vnpay/return` → kiểm tra `success: true`
5. Kiểm tra DB: `GET /orders/{orderId}` → `paymentStatus = PAID`

---

## 📋 VNPay Response Codes

### vnp_ResponseCode
| Code | Mô tả |
|------|-------|
| `00` | Thành công |
| `24` | Khách hàng hủy giao dịch |
| `51` | Tài khoản không đủ số dư |
| `65` | Vượt hạn mức giao dịch trong ngày |
| `75` | Ngân hàng đang bảo trì |
| `99` | Lỗi khác |

### vnp_TransactionStatus
| Code | Mô tả |
|------|-------|
| `00` | Giao dịch thành công |
| `01` | Giao dịch chưa hoàn tất |
| `02` | Giao dịch bị lỗi |

### IPN RspCode
| Code | Mô tả |
|------|-------|
| `00` | Confirm thành công |
| `01` | Order not found |
| `04` | Invalid amount |
| `97` | Invalid signature |
| `99` | Unknown error |
