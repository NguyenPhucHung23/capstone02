package cap2.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveEditedProductsRequest {

    @NotNull(message = "Edited products are required")
    @Valid
    private List<EditedProductRequest> editedProducts;

    // Fixed: optional layout snapshot update so the scene can be persisted together with product edits.
    private Map<String, Object> sceneData;
    private String reasoning;
    private Map<String, String> reasoningDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditedProductRequest {
        @JsonAlias({"id", "product_id"})
        @NotBlank(message = "Product ID is required")
        private String productId;
        private String name;
        private String category;
        private String modelUrl;
        private Double price;
        private Double x;
        private Double y;
        private Double z;
        private Double rotationX;
        private Double rotationY;
        private Double rotationZ;
        private Double scaleX;
        private Double scaleY;
        private Double scaleZ;
        private Double width;
        private Double depth;
        private Double height;
        private Map<String, Object> metadata;
    }
}
