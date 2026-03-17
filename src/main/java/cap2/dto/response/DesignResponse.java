package cap2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignResponse {

    private String id;
    private String roomType;
    private DimensionsResponse dimensions;
    private String style;
    private String furnitureDensity;
    private String gender;
    private String imageUrl;
    private List<AiProductResponse> recommendedProducts;
    private Instant createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DimensionsResponse {
        private double width;
        private double length;
        private double height;
    }
}
