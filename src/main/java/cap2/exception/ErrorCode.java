package cap2.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    USER_ALREADY_EXISTS(1001, "Email already exists", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT(1002, "Invalid email format", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_WEAK(1003, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}