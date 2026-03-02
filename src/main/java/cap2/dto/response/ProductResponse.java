package cap2.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {

    String id;
    String name;
    String slug;
    String category;

    Double price;
    String currency;
    String priceFormatted;

    String sku;
    String availabilityText;
    Boolean inStock;
    Integer stock;

    String material;
    ColorResponse color;
    List<String> styles;
    String origin;

    DimensionsResponse dimensions;
    String dimensionsRaw;

    String description;

    List<String> careInstructions;
    List<String> notes;

    List<String> images;

    String sourceUrl;
    String sourceProvider;

    Instant createdAt;
    Instant updatedAt;

    @Data
    @Builder
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ColorResponse {
        String name;
        String hex;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DimensionsResponse {
        Double width;
        Double height;
        Double depth;
        String unit;
    }
}
