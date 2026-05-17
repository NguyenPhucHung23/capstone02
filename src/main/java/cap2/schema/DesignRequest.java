package cap2.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "design_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignRequest {

    @Id
    private String id;

    private String userId;

    private String roomType;
    private Dimensions dimensions;
    private String style;
    private String furnitureDensity;
    private String gender;
    private int age;
    private String imageUrl;

    private List<String> dominantColors;
    private String colorTone;
    private String detectedStyle;

    private String reasoning;
    private Map<String, String> reasoningDetails;
    private String warning;
    private String densityApplied;

    private List<String> recommendedProductIds;
    private List<ProductSnapshot> recommendedProductSnapshots;

    private Map<String, Object> layout;
    
    @CreatedDate
    private Instant createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dimensions {
        private double width;
        private double length;
        private double height;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSnapshot {
        private String productId;
        private String name;
        private String category;
        private Double price;
        private String imageUrl;
        private String reasoning;
        private Double rankingScore;
        private List<String> styles;
        private List<String> colors;
    }
}
