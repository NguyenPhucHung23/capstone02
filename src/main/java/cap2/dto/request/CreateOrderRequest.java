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
    String shippingAddress;  // Địa chỉ chi tiết (số nhà, tên đường...)
    String shippingCity;     // Thành phố (null nếu dùng province)
    String shippingProvince; // Tỉnh (null nếu dùng city) - chỉ 1 trong 2
    String shippingWard;     // Phường/Xã (bắt buộc)

    @NotNull(message = "Phương thức thanh toán không được để trống")
    String paymentMethod;

    String discountCode;

    String note;
}


