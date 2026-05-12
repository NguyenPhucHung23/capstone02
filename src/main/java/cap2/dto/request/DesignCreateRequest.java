package cap2.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^(Living Room|Bedroom)$", message = "Room type must be either 'Living Room' or 'Bedroom'")
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
        @jakarta.validation.constraints.DecimalMin(value = "2.0", message = "Width phải từ 2m trở lên")
        @jakarta.validation.constraints.DecimalMax(value = "10.0", message = "Width không được vượt quá 10m")
        private double width;

        @jakarta.validation.constraints.DecimalMin(value = "2.0", message = "Length phải từ 2m trở lên")
        @jakarta.validation.constraints.DecimalMax(value = "12.0", message = "Length không được vượt quá 12m")
        private double length;

        @jakarta.validation.constraints.DecimalMin(value = "2.0", message = "Height phải từ 2m trở lên")
        @jakarta.validation.constraints.DecimalMax(value = "4.0", message = "Height không được vượt quá 4m")
        private double height;
    }
}
