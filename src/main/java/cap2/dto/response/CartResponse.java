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
public class CartResponse {

    String id;
    String userId;
    List<CartItemResponse> items;
    Integer totalItems;
    Double totalPrice;
    Instant createdAt;
    Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CartItemResponse {
        String productId;
        String productName;
        String productImage;
        Double price;
        Integer quantity;
        Double subtotal;
    }
}
