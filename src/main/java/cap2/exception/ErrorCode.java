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

    CART_NOT_FOUND(1013, "Không tìm thấy giỏ hàng", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND(1014, "Không tìm thấy sản phẩm trong giỏ hàng", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}