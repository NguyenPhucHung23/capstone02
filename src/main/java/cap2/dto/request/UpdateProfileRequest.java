package cap2.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    String fullName;

    @Pattern(
            regexp = "^[0-9]{9,11}$",
            message = "Phone number must be 9-11 digits"
    )
    String phone;


    @Size(max = 255, message = "Address must not exceed 255 characters")
    String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    String city;       // Tỉnh/Thành phố

    @Size(max = 100, message = "District must not exceed 100 characters")
    String district;   // Quận/Huyện

    @Size(max = 100, message = "Ward must not exceed 100 characters")
    String ward;       // Phường/Xã

    @Pattern(
            regexp = "^(male|female|other)$",
            message = "Gender must be male, female or other"
    )
    String gender;
}
