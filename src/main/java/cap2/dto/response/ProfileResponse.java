package cap2.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
    String id;
    String userId;
    String fullName;
    String phone;
    String email;
    String address;
    String city;       // Tỉnh/Thành phố
    String district;   // Quận/Huyện
    String ward;       // Phường/Xã
    String gender;
}
