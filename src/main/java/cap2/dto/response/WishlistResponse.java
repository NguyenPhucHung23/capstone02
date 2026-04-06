package cap2.dto.response;

import cap2.schema.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WishlistResponse {

    String id;
    String userId;
    List<PublicProductResponse> products;
    Integer totalItems;
}
