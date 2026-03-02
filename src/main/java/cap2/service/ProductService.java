package cap2.service;

import cap2.dto.request.ProductRequest;
import cap2.dto.response.PageResponse;
import cap2.dto.response.ProductResponse;
import cap2.exception.AppException;
import cap2.exception.ErrorCode;
import cap2.repository.ProductRepository;
import cap2.schema.Product;
import cap2.util.SecurityUtils;
import cap2.util.SlugUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

    ProductRepository productRepository;

    /**
     * Tạo hoặc cập nhật product
     * Nếu đã tồn tại (sourceProvider + sourceUrl) → update
     * Nếu chưa tồn tại → create
     */
    public ProductResponse createOrUpdateProduct(ProductRequest request) {
        SecurityUtils.checkAdminRole();

        String sourceProvider = request.getSourceProvider().trim();
        String sourceUrl = request.getSourceUrl().trim();

        // Tìm product existing theo sourceProvider + sourceUrl
        Optional<Product> existingProduct = productRepository
                .findBySourceProviderAndSourceUrl(sourceProvider, sourceUrl);

        Product product;
        boolean isUpdate = existingProduct.isPresent();

        if (isUpdate) {
            // UPDATE existing product
            product = existingProduct.get();
            updateProductFields(product, request);
            product.setUpdatedAt(Instant.now());
            log.info("Updating existing product: {}", product.getId());
        } else {
            // CREATE new product
            product = buildNewProduct(request);
            log.info("Creating new product with source: {} - {}", sourceProvider, sourceUrl);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product saved with ID: {}, isUpdate: {}", savedProduct.getId(), isUpdate);

        return mapToProductResponse(savedProduct);
    }

    /**
     * Lấy product theo ID
     */
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return mapToProductResponse(product);
    }

    /**
     * Lấy danh sách products (phân trang)
     */
    public PageResponse<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findAll(pageable);

        return PageResponse.<ProductResponse>builder()
                .content(productPage.getContent().stream()
                        .map(this::mapToProductResponse)
                        .toList())
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .build();
    }

    /**
     * Xóa product theo ID (chỉ ADMIN)
     */
    public void deleteProduct(String id) {
        SecurityUtils.checkAdminRole();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.delete(product);
        log.info("Deleted product: {}", id);
    }

    // ===== Helper methods =====

    private Product buildNewProduct(ProductRequest request) {
        Instant now = Instant.now();
        String slug = SlugUtils.toUniqueSlug(request.getName(), String.valueOf(now.toEpochMilli()));

        return Product.builder()
                .name(request.getName())
                .slug(slug)
                .category(request.getCategory())
                .price(request.getPrice())
                .currency(request.getCurrency() != null ? request.getCurrency() : "VND")
                .priceFormatted(request.getPriceFormatted())
                .sku(request.getSku())
                .availabilityText(request.getAvailabilityText())
                .inStock(request.getInStock())
                .stock(request.getStock())
                .material(request.getMaterial())
                .color(mapColorRequest(request.getColor()))
                .styles(request.getStyles())
                .origin(request.getOrigin())
                .dimensions(mapDimensionsRequest(request.getDimensions()))
                .dimensionsRaw(request.getDimensionsRaw())
                .description(request.getDescription())
                .careInstructions(request.getCareInstructions())
                .notes(request.getNotes())
                .images(request.getImages())
                .sourceUrl(request.getSourceUrl().trim())
                .sourceProvider(request.getSourceProvider().trim())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private void updateProductFields(Product product, ProductRequest request) {
        // Update slug nếu name thay đổi
        if (!product.getName().equals(request.getName())) {
            product.setSlug(SlugUtils.toUniqueSlug(request.getName(),
                    String.valueOf(Instant.now().toEpochMilli())));
        }

        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setCurrency(request.getCurrency() != null ? request.getCurrency() : "VND");
        product.setPriceFormatted(request.getPriceFormatted());
        product.setSku(request.getSku());
        product.setAvailabilityText(request.getAvailabilityText());
        product.setInStock(request.getInStock());
        product.setStock(request.getStock());
        product.setMaterial(request.getMaterial());
        product.setColor(mapColorRequest(request.getColor()));
        product.setStyles(request.getStyles());
        product.setOrigin(request.getOrigin());
        product.setDimensions(mapDimensionsRequest(request.getDimensions()));
        product.setDimensionsRaw(request.getDimensionsRaw());
        product.setDescription(request.getDescription());
        product.setCareInstructions(request.getCareInstructions());
        product.setNotes(request.getNotes());
        product.setImages(request.getImages());
        // Không update sourceUrl và sourceProvider (đây là key)
    }

    private Product.Color mapColorRequest(ProductRequest.ColorRequest colorRequest) {
        if (colorRequest == null) {
            return null;
        }
        return Product.Color.builder()
                .name(colorRequest.getName())
                .hex(colorRequest.getHex())
                .build();
    }

    private Product.Dimensions mapDimensionsRequest(ProductRequest.DimensionsRequest dimRequest) {
        if (dimRequest == null) {
            return null;
        }
        return Product.Dimensions.builder()
                .width(dimRequest.getWidth())
                .height(dimRequest.getHeight())
                .depth(dimRequest.getDepth())
                .unit(dimRequest.getUnit() != null ? dimRequest.getUnit() : "cm")
                .build();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .category(product.getCategory())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .priceFormatted(product.getPriceFormatted())
                .sku(product.getSku())
                .availabilityText(product.getAvailabilityText())
                .inStock(product.getInStock())
                .stock(product.getStock())
                .material(product.getMaterial())
                .color(mapColorResponse(product.getColor()))
                .styles(product.getStyles())
                .origin(product.getOrigin())
                .dimensions(mapDimensionsResponse(product.getDimensions()))
                .dimensionsRaw(product.getDimensionsRaw())
                .description(product.getDescription())
                .careInstructions(product.getCareInstructions())
                .notes(product.getNotes())
                .images(product.getImages())
                .sourceUrl(product.getSourceUrl())
                .sourceProvider(product.getSourceProvider())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private ProductResponse.ColorResponse mapColorResponse(Product.Color color) {
        if (color == null) {
            return null;
        }
        return ProductResponse.ColorResponse.builder()
                .name(color.getName())
                .hex(color.getHex())
                .build();
    }

    private ProductResponse.DimensionsResponse mapDimensionsResponse(Product.Dimensions dimensions) {
        if (dimensions == null) {
            return null;
        }
        return ProductResponse.DimensionsResponse.builder()
                .width(dimensions.getWidth())
                .height(dimensions.getHeight())
                .depth(dimensions.getDepth())
                .unit(dimensions.getUnit())
                .build();
    }
}
