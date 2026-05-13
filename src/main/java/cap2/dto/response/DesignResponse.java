package cap2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

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
    private String reasoning;
    private java.util.Map<String, String> reasoningDetails;
    private List<AiProductResponse> recommendedProducts;
    private List<String> dominantColors;
    private String colorTone;
    private String detectedStyle;
    private String warning;
    private String densityApplied;

    private Map<String, Object> layout;
    
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
