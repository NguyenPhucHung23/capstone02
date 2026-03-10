package cap2.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProvinceResponse {
    String id;
    String name;
    String type;        // "PROVINCE" hoặc "CITY"
    String typeDisplay; // "Tỉnh" hoặc "Thành phố"
}
