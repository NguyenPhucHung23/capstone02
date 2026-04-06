package cap2.schema;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "reviews")
@CompoundIndexes({
        @CompoundIndex(name = "user_product_unique", def = "{'userId': 1, 'productId': 1}", unique = true)
})
public class Review {

    @Id
    String id;

    String productId;
    String userId;

    /** Mã đơn hàng đã mua sản phẩm – bắt buộc để xác nhận mua hàng */
    String orderCode;

    /** Chấm sao từ 1 đến 5 */
    Integer rating;

    /** Nội dung nhận xét */
    String comment;

    @CreatedDate
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;
}
