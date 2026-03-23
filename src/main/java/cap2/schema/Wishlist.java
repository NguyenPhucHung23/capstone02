package cap2.schema;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "wishlists")
public class Wishlist {

    @Id
    String id;

    @Indexed(unique = true)
    String userId;

    @Builder.Default
    List<String> productIds = new ArrayList<>();

    Instant updatedAt;
}
