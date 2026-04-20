# 📚 API Documentation - Capstone2

**Base URL:** `http://localhost:8000`  
**Auth Header:** `Authorization: Bearer <token>`

---

## 📂 Danh sách tài liệu

| File | Nội dung |
|------|----------|
| [01-auth.md](./01-auth.md) | 🔓 Đăng ký, Đăng nhập |
| [02-user-profile.md](./02-user-profile.md) | 👤 User & Profile (USER + ADMIN) |
| [03-location.md](./03-location.md) | 🗺️ Tỉnh/Thành phố & Phường/Xã |
| [04-product.md](./04-product.md) | 🛒 Sản phẩm (Public + Admin) |
| [05-cart.md](./05-cart.md) | 🛍️ Giỏ hàng |
| [06-order.md](./06-order.md) | 📦 Đơn hàng (User + Admin) |
| [07-payment.md](./07-payment.md) | 💳 Thanh toán VNPay |
| [08-dashboard.md](./08-dashboard.md) | 📊 Admin Dashboard |
| [09-errors.md](./09-errors.md) | ❌ Error Codes |
| [10-product-search.md](./10-product-search.md) | 🔎 Tìm kiếm sản phẩm |
| [11-review.md](./11-review.md) | ⭐ Đánh giá sản phẩm |
| [12-wishlist.md](./12-wishlist.md) | ❤️ Wishlist |
| [13-deployment.md](./13-deployment.md) | 🚀 Hướng dẫn deploy production |

---

## 🔑 Roles

| Role | Quyền |
|------|-------|
| `USER` | Xem/sửa thông tin của mình, giỏ hàng, đặt hàng, thanh toán |
| `ADMIN` | Toàn quyền: quản lý users, products, orders, dashboard |

---

## ⚡ Quick Reference

| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| POST | `/users` | Đăng ký | ❌ |
| POST | `/auth/login` | Đăng nhập | ❌ |
| GET | `/users/me` | Thông tin user hiện tại | ✅ USER |
| GET | `/profiles/me` | Profile hiện tại | ✅ USER |
| PUT | `/profiles/me` | Cập nhật profile | ✅ USER |
| GET | `/locations/provinces` | Danh sách tỉnh/thành | ❌ |
| GET | `/products` | Danh sách sản phẩm | ❌ |
| GET | `/products/{id}` | Chi tiết sản phẩm | ❌ |
| GET | `/cart` | Xem giỏ hàng | ✅ USER |
| POST | `/cart/items` | Thêm vào giỏ | ✅ USER |
| POST | `/orders` | Tạo đơn hàng | ✅ USER |
| POST | `/payments/vnpay/create/{orderId}` | Tạo URL thanh toán | ✅ USER |
| GET | `/admin/dashboard` | Dashboard tổng hợp | ✅ ADMIN |
