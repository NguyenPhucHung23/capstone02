package cap2.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email không hợp lệ")
    String email;

    @NotBlank(message = "OTP is required")
    String otp;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    String newPassword;
}