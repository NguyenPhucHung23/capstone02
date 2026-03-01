package cap2.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    USER_ALREADY_EXISTS(1001, "Email already exists", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT(1002, "Invalid email format", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_WEAK(1003, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1004, "User not found", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD(1005, "Invalid password", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1006, "Invalid token", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN(1007, "Expired token", HttpStatus.UNAUTHORIZED),
    PROFILE_NOT_FOUND(1008, "Profile not found", HttpStatus.NOT_FOUND),
    INVALID_ROLE(1009, "Invalid role", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1010, "You don't have permission", HttpStatus.FORBIDDEN);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}