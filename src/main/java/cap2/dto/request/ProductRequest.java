package cap2.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    String name;

    String category;

    @PositiveOrZero(message = "Price must be positive or zero")
    Double price;

    String currency;
    String priceFormatted;

    String sku;
    String availabilityText;
    Boolean inStock;
    Integer stock;

    String material;

    @Valid
    ColorRequest color;

    List<String> styles;
    String origin;

    @Valid
    DimensionsRequest dimensions;

    String dimensionsRaw;

    String description;

    List<String> careInstructions;
    List<String> notes;

    List<String> images;

    @NotBlank(message = "Source URL is required")
    String sourceUrl;

    @NotBlank(message = "Source provider is required")
    String sourceProvider;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ColorRequest {
        String name;
        String hex;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DimensionsRequest {
        Double width;
        Double height;
        Double depth;
        String unit;
    }
}
