package cap2.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document(collection = "projects_3d")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project3D {

    @Id
    private String id;

    private String designRequestId;
    private String userId;
    private String name;

    // Fixed: store the final 3D scene/layout snapshot separately from the original design request.
    private Map<String, Object> sceneData;

    // Fixed: store edited products after user adjusts positions in the 3D scene.
    @Builder.Default
    private List<EditedProduct> editedProducts = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditedProduct {
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
