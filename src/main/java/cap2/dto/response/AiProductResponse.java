package cap2.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiProductResponse {
    private String id;
    private String name;
    private String category;
    private List<String> styles;
    private Double price;
    private AiProductDimensions dimensions;
    private List<String> colors;
    private String imageUrl;
    private String reasoning;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AiProductDimensions {
        private Double width;
        private Double depth;
        private Double height;
    }
}
