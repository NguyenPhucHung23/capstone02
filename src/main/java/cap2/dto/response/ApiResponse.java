package cap2.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    boolean success;
    int code;
    String message;
    T data;

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, 0, message, data);
    }
}