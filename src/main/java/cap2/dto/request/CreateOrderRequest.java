package cap2.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderRequest {

    // Thông tin giao hàng - nếu không gửi sẽ lấy từ Profile
    String customerName;
    String customerPhone;
    String shippingAddress;
    String shippingCity;
    String shippingDistrict;
    String shippingWard;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    String paymentMethod;

    String discountCode;

    String note;
}
