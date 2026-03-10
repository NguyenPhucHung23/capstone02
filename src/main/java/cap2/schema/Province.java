package cap2.schema;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "cities")
public class Province {

    @Id
    String id;

    @Indexed(unique = true)
    String name;   // Tên tỉnh hoặc thành phố

    Type type;     // Phân loại: tỉnh hoặc thành phố

    public enum Type {
        PROVINCE,  // Tỉnh
        CITY       // Thành phố trực thuộc trung ương
    }
}
