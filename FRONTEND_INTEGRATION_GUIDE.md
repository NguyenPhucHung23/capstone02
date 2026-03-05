# 🌐 VNPAY FRONTEND INTEGRATION GUIDE

## 📋 TỔNG QUAN

Khi tích hợp với Frontend, có 2 cách xử lý Return URL:

1. **Option 1:** VNPay return về Frontend → Frontend xử lý UI
2. **Option 2:** VNPay return về Backend → Backend verify rồi redirect sang Frontend

---

## ✅ OPTION 1: RETURN VỀ FRONTEND (RECOMMENDED)

### Backend Configuration

**File: `src/main/resources/application.properties`**
```properties
# Return về frontend payment result page
vnpay.returnUrl=http://localhost:3000/payment/result

# Production
# vnpay.returnUrl=https://yourdomain.com/payment/result
```

### Frontend Implementation

#### 1. Tạo Payment Result Page

**React Example (`pages/PaymentResult.jsx`):**
```jsx
import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import axios from 'axios';

function PaymentResult() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [result, setResult] = useState(null);

  useEffect(() => {
    const processPayment = async () => {
      // Lấy params từ VNPay
      const responseCode = searchParams.get('vnp_ResponseCode');
      const transactionStatus = searchParams.get('vnp_TransactionStatus');
      const orderCode = searchParams.get('vnp_TxnRef');
      const amount = searchParams.get('vnp_Amount');
      const secureHash = searchParams.get('vnp_SecureHash');

      // Verify với backend
      try {
        const response = await axios.post('/api/payments/vnpay/verify', {
          responseCode,
          transactionStatus,
          orderCode,
          amount,
          secureHash,
          // Pass tất cả params để backend verify
          ...Object.fromEntries(searchParams)
        });

        setResult(response.data);
        setLoading(false);

        // Redirect sau 3 giây
        setTimeout(() => {
          if (responseCode === '00' && transactionStatus === '00') {
            navigate(`/orders/${orderCode}`);
          } else {
            navigate('/orders');
          }
        }, 3000);
      } catch (error) {
        console.error('Error verifying payment:', error);
        setLoading(false);
      }
    };

    processPayment();
  }, [searchParams, navigate]);

  if (loading) {
    return <div>Đang xử lý kết quả thanh toán...</div>;
  }

  const isSuccess = result?.data?.success === 'true';

  return (
    <div className="payment-result">
      {isSuccess ? (
        <div className="success">
          <h1>✅ Thanh toán thành công!</h1>
          <p>Mã đơn hàng: {result?.data?.orderCode}</p>
          <p>Số tiền: {parseInt(result?.data?.amount) / 100} VND</p>
          <p>Đang chuyển đến trang đơn hàng...</p>
        </div>
      ) : (
        <div className="failed">
          <h1>❌ Thanh toán thất bại</h1>
          <p>Mã lỗi: {searchParams.get('vnp_ResponseCode')}</p>
          <p>{result?.data?.message}</p>
          <button onClick={() => navigate('/orders')}>
            Quay lại danh sách đơn hàng
          </button>
        </div>
      )}
    </div>
  );
}

export default PaymentResult;
```

#### 2. Backend Verify Endpoint (Tùy chọn)

Nếu frontend muốn verify lại:

```java
@PostMapping("/verify")
public ApiResponse<Map<String, String>> verifyPayment(@RequestBody Map<String, String> params) {
    // Verify signature
    if (!vnPayService.verifyCallbackSignature(params)) {
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    String orderCode = params.get("vnp_TxnRef");
    String responseCode = params.get("vnp_ResponseCode");
    String transactionStatus = params.get("vnp_TransactionStatus");

    // Update order nếu chưa update
    Order order = orderRepository.findByOrderCode(orderCode)
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

    if (order.getPaymentStatus() != Order.PaymentStatus.PAID) {
        updateOrderPaymentStatus(order, responseCode, transactionStatus);
    }

    Map<String, String> data = new HashMap<>();
    data.put("orderCode", orderCode);
    data.put("success", String.valueOf("00".equals(responseCode) && "00".equals(transactionStatus)));
    data.put("message", "00".equals(responseCode) ? "Thanh toan thanh cong" : "Thanh toan that bai");

    return ApiResponse.ok("Verify payment", data);
}
```

---

## ✅ OPTION 2: BACKEND REDIRECT SANG FRONTEND

### Backend Configuration

**File: `application.properties`**
```properties
# Vẫn return về backend
vnpay.returnUrl=http://localhost:8000/payments/vnpay/return

# Frontend URLs
app.frontend.url=http://localhost:3000
app.frontend.payment.success=/payment/success
app.frontend.payment.failed=/payment/failed
```

### Backend Controller

```java
@GetMapping("/return")
@Transactional
public void vnpayReturn(@RequestParam Map<String, String> params, 
                        HttpServletResponse response) throws IOException {
    log.info("VNPay Return callback: {}", params);

    // Verify signature
    if (!vnPayService.verifyCallbackSignature(params)) {
        log.error("VNPay Return: Invalid signature");
        response.sendRedirect("http://localhost:3000/payment/failed?error=invalid_signature");
        return;
    }

    String txnRef = params.get("vnp_TxnRef");
    String responseCode = params.get("vnp_ResponseCode");
    String transactionStatus = params.get("vnp_TransactionStatus");

    // Update order
    Order order = orderRepository.findByOrderCode(txnRef)
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

    updateOrderPaymentStatus(order, responseCode, transactionStatus);

    // Redirect sang frontend
    if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
        response.sendRedirect("http://localhost:3000/payment/success?orderId=" + order.getId() + "&orderCode=" + txnRef);
    } else {
        response.sendRedirect("http://localhost:3000/payment/failed?orderId=" + order.getId() + "&errorCode=" + responseCode);
    }
}
```

### Frontend Pages

**Success Page (`pages/PaymentSuccess.jsx`):**
```jsx
import { useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';

function PaymentSuccess() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const orderId = searchParams.get('orderId');
  const orderCode = searchParams.get('orderCode');

  useEffect(() => {
    // Auto redirect sau 3 giây
    const timer = setTimeout(() => {
      navigate(`/orders/${orderId}`);
    }, 3000);

    return () => clearTimeout(timer);
  }, [navigate, orderId]);

  return (
    <div className="payment-success">
      <div className="success-icon">✅</div>
      <h1>Thanh toán thành công!</h1>
      <p>Mã đơn hàng: {orderCode}</p>
      <p>Đang chuyển đến trang chi tiết đơn hàng...</p>
      <button onClick={() => navigate(`/orders/${orderId}`)}>
        Xem đơn hàng ngay
      </button>
    </div>
  );
}

export default PaymentSuccess;
```

**Failed Page (`pages/PaymentFailed.jsx`):**
```jsx
import { useSearchParams, useNavigate } from 'react-router-dom';

function PaymentFailed() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const errorCode = searchParams.get('errorCode');

  const getErrorMessage = (code) => {
    const messages = {
      '24': 'Giao dịch bị hủy',
      '51': 'Tài khoản không đủ số dư',
      '65': 'Vượt quá hạn mức giao dịch',
      '75': 'Ngân hàng đang bảo trì',
    };
    return messages[code] || 'Thanh toán thất bại';
  };

  return (
    <div className="payment-failed">
      <div className="error-icon">❌</div>
      <h1>Thanh toán thất bại</h1>
      <p>{getErrorMessage(errorCode)}</p>
      <p>Mã lỗi: {errorCode}</p>
      <button onClick={() => navigate('/orders')}>
        Quay lại danh sách đơn hàng
      </button>
    </div>
  );
}

export default PaymentFailed;
```

---

## 🔄 FULL FLOW VỚI FRONTEND

### 1. User Click "Thanh toán VNPay"

**Frontend:**
```javascript
const handlePaymentVNPay = async (orderId) => {
  try {
    const response = await axios.post(
      `/api/payments/vnpay/create/${orderId}`,
      null,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    
    const paymentUrl = response.data.data.paymentUrl;
    
    // Redirect sang VNPay
    window.location.href = paymentUrl;
  } catch (error) {
    alert('Không thể tạo link thanh toán');
  }
};
```

### 2. User Thanh toán trên VNPay

User nhập thông tin thẻ, OTP, submit...

### 3. VNPay Redirect về Return URL

**Option 1:** `http://localhost:3000/payment/result?vnp_...`

**Option 2:** `http://localhost:8000/payments/vnpay/return?vnp_...` → Backend redirect → `http://localhost:3000/payment/success?orderId=...`

### 4. Frontend Hiển thị kết quả

- Success page: Hiển thị thông báo thành công
- Failed page: Hiển thị lỗi
- Auto redirect về order detail sau 3-5 giây

### 5. User Xem đơn hàng

Frontend call: `GET /api/orders/{orderId}`

**Expected:**
```json
{
  "data": {
    "orderCode": "ORD20260305123456789",
    "paymentStatus": "PAID",
    "status": "CONFIRMED"
  }
}
```

---

## 🎯 GỢI Ý SỬ DỤNG

### Development (localhost):
```properties
vnpay.returnUrl=http://localhost:3000/payment/result
```

### Production:
```properties
vnpay.returnUrl=https://yourdomain.com/payment/result
```

### Testing:
- Option 1 (Frontend): Dễ debug, frontend control UI
- Option 2 (Backend redirect): Backend control flow, đơn giản hơn

---

## 📝 TÓM TẮT

**Recommended cho Frontend:**
1. VNPay return về Frontend page (`/payment/result`)
2. Frontend parse query params
3. Frontend call backend verify API (optional)
4. Hiển thị success/failed UI
5. Auto redirect sang order detail

**Lợi ích:**
- Frontend control UI/UX
- Backend chỉ lo logic verify và update DB
- Tách biệt rõ ràng giữa UI và business logic

**Chúc bạn tích hợp thành công! 🚀**
