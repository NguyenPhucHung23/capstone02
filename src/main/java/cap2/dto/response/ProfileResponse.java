package cap2.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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
    String city;       // Thành phố (null nếu dùng province)
    String province;   // Tỉnh (null nếu dùng city)
    String ward;       // Phường/Xã
    String gender;
    LocalDate dateOfBirth;  // Ngày sinh
}
