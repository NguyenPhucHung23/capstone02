package cap2.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "design_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignRequest {

    @Id
    private String id;

    private String roomType;
    private Dimensions dimensions;
    private String style;
    private String furnitureDensity;
    private String gender;
    private List<String> imageUrls;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dimensions {
        private double width;
        private double length;
        private double height;
    }
}
