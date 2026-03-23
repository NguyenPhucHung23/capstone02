package cap2.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {

    String id;
    String productId;
    String userId;
    String orderCode;
    Integer rating;
    String comment;
    Instant createdAt;
    Instant updatedAt;

    /** Thống kê tổng hợp đánh giá của 1 sản phẩm */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RatingSummary {
        String productId;
        Double avgRating;
        Long reviewCount;
        /** Phân bố sao: {1: 5, 2: 3, 3: 10, 4: 20, 5: 62} */
        Map<Integer, Long> distribution;
    }
}
