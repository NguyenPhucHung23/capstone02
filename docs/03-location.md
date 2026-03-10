# 🗺️ Location API

**Base URL:** `http://localhost:8000`  
> Không cần token

---

## 1. Lấy tất cả tỉnh/thành phố
```
GET /locations/provinces
```

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Lấy danh sách tỉnh/thành phố thành công",
  "data": [
    { "id": "...", "name": "Hà Nội",       "type": "CITY",     "typeDisplay": "Thành phố" },
    { "id": "...", "name": "Hồ Chí Minh",  "type": "CITY",     "typeDisplay": "Thành phố" },
    { "id": "...", "name": "Hải Phòng",    "type": "CITY",     "typeDisplay": "Thành phố" },
    { "id": "...", "name": "Đà Nẵng",      "type": "CITY",     "typeDisplay": "Thành phố" },
    { "id": "...", "name": "Huế",          "type": "CITY",     "typeDisplay": "Thành phố" },
    { "id": "...", "name": "Cần Thơ",      "type": "CITY",     "typeDisplay": "Thành phố" },
    { "id": "...", "name": "Nghệ An",      "type": "PROVINCE", "typeDisplay": "Tỉnh" },
    { "id": "...", "name": "Thanh Hóa",    "type": "PROVINCE", "typeDisplay": "Tỉnh" }
  ]
}
```

---

## 2. Chỉ lấy 6 thành phố trực thuộc TW
```
GET /locations/provinces/cities
```

> Trả về: Hà Nội, Hải Phòng, Huế, Đà Nẵng, Cần Thơ, Hồ Chí Minh

---

## 3. Chỉ lấy 28 tỉnh
```
GET /locations/provinces/provinces-only
```

> Trả về: An Giang, Bắc Ninh, Cà Mau, Cao Bằng, Đắk Lắk, Điện Biên, Đồng Nai, Đồng Tháp, Gia Lai, Hà Tĩnh, Hưng Yên, Khánh Hòa, Lai Châu, Lạng Sơn, Lào Cai, Lâm Đồng, Nghệ An, Ninh Bình, Phú Thọ, Quảng Ngãi, Quảng Ninh, Quảng Trị, Sơn La, Tây Ninh, Thái Nguyên, Thanh Hóa, Tuyên Quang, Vĩnh Long

---

## 4. Chi tiết tỉnh/thành theo ID
```
GET /locations/provinces/{id}
```

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Lấy thông tin tỉnh/thành phố thành công",
  "data": {
    "id": "abc123...",
    "name": "Đà Nẵng",
    "type": "CITY",
    "typeDisplay": "Thành phố"
  }
}
```

---

## 5. Lấy phường/xã theo ID tỉnh/thành
```
GET /locations/provinces/{id}/wards
```

**Ví dụ:**
```
GET /locations/provinces/{idDaNang}/wards
```

**Response:**
```json
{
  "success": true,
  "code": 0,
  "message": "Lấy danh sách phường/xã thành công",
  "data": [
    { "id": "...", "name": "Phường Hải Châu 1", "cityId": "abc123..." },
    { "id": "...", "name": "Phường Hải Châu 2", "cityId": "abc123..." },
    { "id": "...", "name": "Phường Mỹ An",       "cityId": "abc123..." }
  ]
}
```

---

## 6. Lấy phường/xã theo tên tỉnh/thành
```
GET /locations/wards?provinceName=Đà Nẵng
```

**Ví dụ:**
```
GET /locations/wards?provinceName=Hồ Chí Minh
GET /locations/wards?provinceName=Nghệ An
```

---

## 📌 Hướng dẫn dùng địa chỉ

| Field | Mô tả | Ví dụ |
|-------|-------|-------|
| `city` | 1 trong 6 thành phố trực thuộc TW | `Đà Nẵng` |
| `province` | 1 trong 28 tỉnh (dùng `city` **hoặc** `province`) | `Nghệ An` |
| `ward` | Phường/Xã thuộc city/province đã chọn | `Phường Hải Châu 1` |
| `address` | Số nhà, tên đường, tổ/thôn... | `123 Đường Lê Duẩn` |

> **Luồng UI gợi ý:**
> 1. Gọi `/locations/provinces` → cho user chọn tỉnh/thành
> 2. Gọi `/locations/wards?provinceName={tên đã chọn}` → cho user chọn phường/xã
> 3. Điền `address` thủ công
