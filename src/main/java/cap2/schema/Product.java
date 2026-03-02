package cap2.schema;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "products")
@CompoundIndex(name = "source_unique_idx", def = "{'sourceProvider': 1, 'sourceUrl': 1}", unique = true)
public class Product {

    @Id
    String id;

    String name;
    String slug;
    String category;

    // Price
    Double price;
    String currency;
    String priceFormatted;

    // Stock
    String sku;
    String availabilityText;
    Boolean inStock;
    Integer stock;

    // Details
    String material;
    Color color;
    List<String> styles;
    String origin;

    // Dimensions
    Dimensions dimensions;
    String dimensionsRaw;

    String description;

    List<String> careInstructions;
    List<String> notes;

    List<String> images;

    // Source info
    String sourceUrl;
    String sourceProvider;

    @CreatedDate
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Color {
        String name;
        String hex;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Dimensions {
        Double width;
        Double height;
        Double depth;
        String unit;
    }
}
