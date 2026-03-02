package cap2.schema;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    String address;
    String city;       // Tỉnh/Thành phố
    String district;   // Quận/Huyện
    String ward;       // Phường/Xã
    String gender;
}