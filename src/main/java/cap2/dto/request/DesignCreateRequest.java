package cap2.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignCreateRequest {

    @NotBlank(message = "Room type is required")
    private String roomType;

    @NotNull(message = "Dimensions are required")
    private DimensionsRequest dimensions;

    @NotBlank(message = "Style is required")
    private String style;
    
    private String furnitureDensity;
    
    private String gender;

    private int age;

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
