package cap2.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {

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
    String fullShippingAddress;

    List<OrderItemResponse> items;
    Integer totalItems;

    Double subtotal;
    Double shippingFee;
    Double discount;
    String discountCode;
    Double totalAmount;

    String paymentMethod;
    String paymentStatus;
    String status;

    String statusDisplay;
    String paymentMethodDisplay;
    String paymentStatusDisplay;

    String note;

    Instant createdAt;
    Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OrderItemResponse {
        String productId;
        String productName;
        String productImage;
        Double price;
        Integer quantity;
        Double subtotal;
    }
}
