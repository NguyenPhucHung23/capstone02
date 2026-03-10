package cap2.schema;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "wards")
@CompoundIndex(name = "city_name_idx", def = "{'cityId': 1, 'name': 1}", unique = true)
public class Ward {

    @Id
    String id;

    String name;       // Tên phường/xã

    @Indexed
    String cityId;     // FK tới cities._id
}
