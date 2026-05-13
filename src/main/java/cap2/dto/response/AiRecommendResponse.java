package cap2.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiRecommendResponse {
    private Analysis analysis;
    private List<AiProductResponse> products;
    private String warning;
    private String densityApplied;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Analysis {
        private AnalysisReasoning reasoning;
        private ImageAnalysis imageAnalysis;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnalysisReasoning {
        private String styleJustification;
        private String colorJustification;
        private String densityJustification;
        private String userProfileNote;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageAnalysis {
        private List<String> dominantColors;
        private String colorTone;
        private String detectedStyle;
        private String lightingType;
        private List<String> existingFurnitureCategories;
    }
}
