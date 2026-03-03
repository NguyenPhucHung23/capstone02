package cap2.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    String fullName;

    @Pattern(
            regexp = "^[0-9]{9,11}$",
            message = "Số điện thoại phải có 9-11 chữ số"
    )
    String phone;


    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    String address;

    @Size(max = 100, message = "Tỉnh/Thành phố không được vượt quá 100 ký tự")
    String city;

    @Size(max = 100, message = "Quận/Huyện không được vượt quá 100 ký tự")
    String district;

    @Size(max = 100, message = "Phường/Xã không được vượt quá 100 ký tự")
    String ward;

    @Pattern(
            regexp = "^(male|female|other)$",
            message = "Giới tính phải là male, female hoặc other"
    )
    String gender;
}
