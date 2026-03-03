package cap2.schema;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "orders")
public class Order {

    @Id
    String id;

    String orderCode;

    String userId;
    String customerName;
    String customerEmail;
    String customerPhone;

    String shippingAddress;
    String shippingCity;
    String shippingDistrict;
    String shippingWard;

    List<OrderItem> items;

    Double subtotal;
    Double shippingFee;
    Double discount;
    String discountCode;
    Double totalAmount;

    PaymentMethod paymentMethod;
    PaymentStatus paymentStatus;

    @Builder.Default
    OrderStatus status = OrderStatus.PENDING;

    String note;

    @CreatedDate
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OrderItem {
        String productId;
        String productName;
        String productImage;
        Double price;
        Integer quantity;
        Double subtotal;
    }

    public enum OrderStatus {
        PENDING,      // Đang xử lý
        CONFIRMED,    // Đã xác nhận
        SHIPPING,     // Đang giao
        DELIVERED,    // Đã giao
        CANCELLED     // Đã hủy
    }

    public enum PaymentMethod {
        COD,          // Thanh toán khi nhận hàng
        VNPAY,        // Ví VNPay
        MOMO          // Ví Momo
    }

    public enum PaymentStatus {
        PENDING,      // Chờ thanh toán
        PAID,         // Đã thanh toán
        FAILED        // Thanh toán thất bại
    }
}
