package cap2.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {

    @Email(message = "Email không hợp lệ")
    String email;

    String role;
}
