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
public class Project3DResponse {

    private String id;
    private String designRequestId;
    private String userId;
    private String name;
    private Map<String, Object> sceneData;
    private String reasoning;
    private Map<String, String> reasoningDetails;
    private List<EditedProductResponse> editedProducts;
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditedProductResponse {
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
