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

    @NotBlank(message = "Tên sản phẩm không được để trống")
    String name;

    String category;

    @PositiveOrZero(message = "Giá phải lớn hơn hoặc bằng 0")
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

    @NotBlank(message = "URL nguồn không được để trống")
    String sourceUrl;

    @NotBlank(message = "Nhà cung cấp nguồn không được để trống")
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
