package cap2.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignCreateRequest {

    private String roomType;
    private DimensionsRequest dimensions;
    private String style;
    private String furnitureDensity;
    private String gender;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DimensionsRequest {
        private double width;
        private double length;
        private double height;
    }
}
