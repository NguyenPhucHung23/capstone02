package cap2.schema;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "profiles")
public class Profile {

    @Id
    String id;

    String userId;
    String fullName;
    String phone;
    String address;    // Địa chỉ chi tiết (số nhà, tên đường, thôn/tổ...)
    String city;       // Thành phố (nếu chọn thành phố)
    String province;   // Tỉnh (nếu chọn tỉnh) - chỉ 1 trong city/province
    String ward;       // Phường/Xã
    String gender;
    LocalDate dateOfBirth;  // Ngày sinh
}