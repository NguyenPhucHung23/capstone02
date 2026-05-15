package cap2.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("ranking_score")
    private Double rankingScore;

    @JsonAlias({"model_url", "glbUrl", "glb_url", "modelURL"})
    private String modelUrl;

    @JsonAlias({"ranking_score", "rank_score", "rankingScore", "score", "matchScore"})
    private Double rankingScore;

    @JsonAlias({"style_score", "styleScore"})
    private Double styleScore;

    @JsonAlias({"color_score", "colorScore"})
    private Double colorScore;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AiProductDimensions {
        @JsonAlias({"widthM", "width_m", "w"})
        private Double width;
        @JsonAlias({"depthM", "depth_m", "length", "lengthM", "length_m", "d"})
        private Double depth;
        @JsonAlias({"heightM", "height_m", "h"})
        private Double height;
    }
}
