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
    String reviewerName;
    String orderCode;
    Integer rating;
    String comment;
    Instant createdAt;
    Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RatingSummary {
        String productId;
        Double avgRating;
        Long reviewCount;
        Map<Integer, Long> distribution;
    }
}
