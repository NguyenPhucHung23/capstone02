package cap2.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),

    USER_ALREADY_EXISTS(1001, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT(1002, "Email không hợp lệ", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_WEAK(1003, "Mật khẩu phải có ít nhất {min} ký tự", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1004, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD(1005, "Mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1006, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),

    EXPIRED_TOKEN(1007, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),

    PROFILE_NOT_FOUND(1008, "Không tìm thấy hồ sơ", HttpStatus.NOT_FOUND),
    INVALID_ROLE(1009, "Vai trò không hợp lệ", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED(1010, "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1011, "Chưa đăng nhập", HttpStatus.UNAUTHORIZED),

    PRODUCT_NOT_FOUND(1012, "Không tìm thấy sản phẩm", HttpStatus.NOT_FOUND),
    DESIGN_REQUEST_NOT_FOUND(1026, "Không tìm thấy yêu cầu thiết kế", HttpStatus.NOT_FOUND),

    CART_NOT_FOUND(1013, "Không tìm thấy giỏ hàng", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND(1014, "Không tìm thấy sản phẩm trong giỏ hàng", HttpStatus.NOT_FOUND),
    CART_EMPTY(1015, "Giỏ hàng trống", HttpStatus.BAD_REQUEST),

    ORDER_NOT_FOUND(1016, "Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND),
    ORDER_CANNOT_CANCEL(1017, "Không thể hủy đơn hàng này", HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_CANCELLED(1018, "Đơn hàng đã bị hủy", HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_DELIVERED(1019, "Đơn hàng đã được giao", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_METHOD(1020, "Phương thức thanh toán không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(1021, "Trạng thái đơn hàng không hợp lệ", HttpStatus.BAD_REQUEST),
    MISSING_SHIPPING_INFO(1022, "Vui lòng cung cấp đầy đủ thông tin giao hàng", HttpStatus.BAD_REQUEST),
    INVALID_SHIPPING_ADDRESS(1023, "Địa chỉ giao hàng không hợp lệ: không thể chọn cả city và province cùng lúc", HttpStatus.BAD_REQUEST),
    PROVINCE_NOT_FOUND(1024, "Không tìm thấy tỉnh/thành phố", HttpStatus.NOT_FOUND),
    WARD_NOT_FOUND(1025, "Không tìm thấy phường/xã", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}