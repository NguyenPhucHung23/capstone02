package cap2.exception;

import cap2.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException e) {
        var ec = e.getErrorCode();
        log.warn("AppException: {} - {}", ec.getCode(), ec.getMessage());
        return ResponseEntity.status(ec.getStatusCode())
                .body(new ApiResponse<>(false, ec.getCode(), ec.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("Validation error: {}", msg);
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, 4000, msg, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleOther(Exception e) {
        // LOG CHI TIẾT LỖI ĐỂ DEBUG
        log.error("Uncategorized error: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
        return ResponseEntity.status(500)
                .body(new ApiResponse<>(false, 9999, "Uncategorized error: " + e.getMessage(), null));
    }
}