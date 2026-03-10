package cap2.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    @Pattern(
            regexp = "^[\\p{L}\\s]+$",
            message = "Họ tên chỉ được chứa chữ cái và khoảng trắng"
    )
    String fullName;

    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9,10}$",
            message = "Số điện thoại không hợp lệ (VD: 0912345678 hoặc +84912345678)"
    )
    String phone;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    String address;

    @Size(max = 100, message = "Thành phố không được vượt quá 100 ký tự")
    String city;

    @Size(max = 100, message = "Tỉnh không được vượt quá 100 ký tự")
    String province;

    @Size(max = 100, message = "Phường/Xã không được vượt quá 100 ký tự")
    String ward;

    @Pattern(
            regexp = "^(male|female|other)$",
            message = "Giới tính phải là male, female hoặc other"
    )
    String gender;

    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    LocalDate dateOfBirth;  // Ngày sinh
}
