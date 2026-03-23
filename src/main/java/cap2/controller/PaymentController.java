package cap2.controller;

import cap2.dto.response.ApiResponse;
import cap2.exception.AppException;
import cap2.exception.ErrorCode;
import cap2.repository.OrderRepository;
import cap2.schema.Order;
import cap2.service.OrderService;
import cap2.service.VnPayService;
import cap2.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/payments/vnpay")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    OrderRepository orderRepository;
    VnPayService vnPayService;
    OrderService orderService;

    @PostMapping("/create/{orderId}")
    public ApiResponse<Map<String, String>> createPaymentUrl(@PathVariable String orderId,
                                                             HttpServletRequest request) {
        String userId = SecurityUtils.getCurrentUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId) && !SecurityUtils.isAdmin()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (order.getPaymentMethod() != Order.PaymentMethod.VNPAY) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }

        if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
            throw new AppException(ErrorCode.ORDER_ALREADY_DELIVERED);
        }

        String ipAddress = resolveClientIp(request);
        long amountVnd = Math.round(order.getTotalAmount());
        String paymentUrl = vnPayService.buildPaymentUrl(
                order.getOrderCode(),
                amountVnd,
                ipAddress,
                "Thanh toan don hang " + order.getOrderCode()
        );

        Map<String, String> data = new HashMap<>();
        data.put("orderId", order.getId());
        data.put("orderCode", order.getOrderCode());
        data.put("paymentUrl", paymentUrl);

        return ApiResponse.ok("Tao url thanh toan VNPay thanh cong", data);
    }

    /**
     * Return URL - Hiển thị kết quả cho user
     * NOTE: Trên localhost, tạm thời UPDATE DB ở đây vì IPN không gọi được
     * Khi deploy production với domain public, nên tách riêng (Return không update, IPN update)
     */
    @GetMapping("/return")
    @Transactional
    public ApiResponse<Map<String, String>> vnpayReturn(@RequestParam Map<String, String> params) {
        log.info("VNPay Return callback: {}", params);

        // Verify signature
        if (!vnPayService.verifyCallbackSignature(params)) {
            log.error("VNPay Return: Invalid signature");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        String amount = params.get("vnp_Amount");

        // Tìm order
        Order order = orderRepository.findByOrderCode(txnRef)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Validate amount
        if (!isAmountValid(order, amount)) {
            log.error("VNPay Return: Invalid amount - expected: {}, received: {}",
                Math.round(order.getTotalAmount() * 100), amount);
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }

        // Check kết quả
        boolean isSuccess = "00".equals(responseCode) && "00".equals(transactionStatus);

        // UPDATE DB (cho localhost - vì IPN không gọi được)
        if (order.getPaymentStatus() != Order.PaymentStatus.PAID) {
            updateOrderPaymentStatus(order, responseCode, transactionStatus);
            log.info("VNPay Return: Updated order {} to {}", order.getOrderCode(), order.getPaymentStatus());
        }

        Map<String, String> data = new HashMap<>();
        data.put("orderCode", order.getOrderCode());
        data.put("amount", amount);
        data.put("responseCode", responseCode);
        data.put("transactionStatus", transactionStatus);
        data.put("success", String.valueOf(isSuccess));
        data.put("message", isSuccess ?
            "Thanh toan thanh cong" :
            "Thanh toan that bai - Ma loi: " + responseCode);

        return ApiResponse.ok("VNPay return callback", data);
    }

    /**
     * IPN URL - Server-to-server notification, CẬP NHẬT database ở đây
     * Phải idempotent (gọi nhiều lần không ảnh hưởng)
     */
    @GetMapping("/ipn")
    @Transactional
    public Map<String, String> vnpayIpn(@RequestParam Map<String, String> params) {
        log.info("VNPay IPN callback: {}", params);

        Map<String, String> response = new HashMap<>();

        // 1. Verify signature
        if (!vnPayService.verifyCallbackSignature(params)) {
            log.error("VNPay IPN: Invalid signature - params: {}", params);
            response.put("RspCode", "97");
            response.put("Message", "Invalid signature");
            return response;
        }

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        String vnpAmount = params.get("vnp_Amount");

        // 2. Tìm order
        Order order = orderRepository.findByOrderCode(txnRef).orElse(null);
        if (order == null) {
            log.error("VNPay IPN: Order not found - txnRef: {}", txnRef);
            response.put("RspCode", "01");
            response.put("Message", "Order not found");
            return response;
        }

        // 3. Validate amount
        if (!isAmountValid(order, vnpAmount)) {
            log.error("VNPay IPN: Invalid amount - expected: {}, received: {}",
                Math.round(order.getTotalAmount() * 100), vnpAmount);
            response.put("RspCode", "04");
            response.put("Message", "Invalid amount");
            return response;
        }

        // 4. Idempotent check - nếu đã PAID thì validate lại và return success
        if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
            log.info("VNPay IPN: Order already paid - txnRef: {}", txnRef);
            response.put("RspCode", "00");
            response.put("Message", "Order already confirmed");
            return response;
        }

        // 5. Update order payment status
        // Theo chuẩn VNPAY: Phải check cả ResponseCode VÀ TransactionStatus
        updateOrderPaymentStatus(order, responseCode, transactionStatus);

        log.info("VNPay IPN: Order updated successfully - txnRef: {}, status: {}",
            txnRef, order.getPaymentStatus());

        response.put("RspCode", "00");
        response.put("Message", "Confirm Success");
        return response;
    }

    /**
     * Cập nhật trạng thái thanh toán dựa trên ResponseCode và TransactionStatus
     *
     * Theo tài liệu VNPAY:
     * - vnp_ResponseCode: Mã phản hồi kết quả giao dịch (00 = không lỗi)
     * - vnp_TransactionStatus: Mã giao dịch tại hệ thống VNPAY (00 = thành công)
     *
     * Chỉ khi CẢ HAI đều = "00" thì mới coi là thanh toán thành công
     */
    private void updateOrderPaymentStatus(Order order, String responseCode, String transactionStatus) {
        boolean wasPaid = order.getPaymentStatus() == Order.PaymentStatus.PAID;

        // Phải check CẢ ResponseCode VÀ TransactionStatus
        if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
            order.setPaymentStatus(Order.PaymentStatus.PAID);

            // Auto confirm order khi thanh toán thành công
            if (order.getStatus() == Order.OrderStatus.PENDING) {
                order.setStatus(Order.OrderStatus.CONFIRMED);
            }

            log.info("Order {} payment SUCCESS", order.getOrderCode());
        } else {
            order.setPaymentStatus(Order.PaymentStatus.FAILED);
            log.warn("Order {} payment FAILED - ResponseCode: {}, TransactionStatus: {}",
                order.getOrderCode(), responseCode, transactionStatus);
        }

        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);

        if (!wasPaid && order.getPaymentStatus() == Order.PaymentStatus.PAID) {
            orderService.applyInventoryOnPaidOrder(order);
        }
    }

    private boolean isAmountValid(Order order, String callbackAmount) {
        if (callbackAmount == null || callbackAmount.isBlank()) {
            return false;
        }

        try {
            long received = Long.parseLong(callbackAmount);
            // VNPAY gửi về amount * 100 (VND không có đơn vị nhỏ hơn)
            long expected = Math.round(order.getTotalAmount() * 100);
            return received == expected;
        } catch (NumberFormatException e) {
            log.error("Invalid amount format: {}", callbackAmount, e);
            return false;
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
