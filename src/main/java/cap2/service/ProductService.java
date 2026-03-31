package cap2.service;

import cap2.dto.request.ProductRequest;
import cap2.dto.request.ProductSearchRequest;
import cap2.dto.response.PageResponse;
import cap2.dto.response.ProductResponse;
import cap2.dto.response.PublicProductResponse;
import cap2.exception.AppException;
import cap2.exception.ErrorCode;
import cap2.repository.ProductRepository;
import cap2.repository.ReviewRepository;
import cap2.schema.Product;
import cap2.util.SecurityUtils;
import cap2.util.SlugUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

    ProductRepository productRepository;
    ReviewRepository reviewRepository;
    MongoTemplate mongoTemplate;

    /**
     * Tạo hoặc cập nhật product
     * Nếu đã tồn tại (sourceProvider + sourceUrl) → update
     * Nếu chưa tồn tại → create
     */
    public ProductResponse createOrUpdateProduct(ProductRequest request) {
        SecurityUtils.checkAdminRole();
        return processProductRequest(request);
    }

    /**
     * Batch import nhiều products cùng lúc
     */
    public List<ProductResponse> batchCreateOrUpdateProducts(List<ProductRequest> requests) {
        SecurityUtils.checkAdminRole();

        List<ProductResponse> responses = new ArrayList<>();
        for (ProductRequest request : requests) {
            try {
                ProductResponse response = processProductRequest(request);
                responses.add(response);
            } catch (Exception e) {
                log.error("Error processing product: {} - {}", request.getName(), e.getMessage());
            }
        }
        log.info("Batch import completed: {}/{} products", responses.size(), requests.size());
        return responses;
    }

    /**
     * Xử lý tạo/cập nhật 1 product
     */
    private ProductResponse processProductRequest(ProductRequest request) {
        String sourceProvider = request.getSourceProvider().trim();
        String sourceUrl = request.getSourceUrl().trim();

        Optional<Product> existingProduct = productRepository
                .findBySourceProviderAndSourceUrl(sourceProvider, sourceUrl);

        Product product;
        boolean isUpdate = existingProduct.isPresent();

        if (isUpdate) {
            product = existingProduct.get();
            updateProductFields(product, request);
            product.setUpdatedAt(Instant.now());
            log.info("Updating existing product: {}", product.getId());
        } else {
            product = buildNewProduct(request);
            log.info("Creating new product with source: {} - {}", sourceProvider, sourceUrl);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product saved with ID: {}, isUpdate: {}", savedProduct.getId(), isUpdate);

        return mapToProductResponse(savedProduct);
    }

    /**
     * Lấy product theo ID (USER - ẩn soldCount, sourceUrl, sourceProvider)
     */
    public PublicProductResponse getProductByIdPublic(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return mapToPublicProductResponse(product);
    }

    /**
     * Lấy danh sách products (USER - ẩn soldCount, sourceUrl, sourceProvider)
     */
    public PageResponse<PublicProductResponse> getAllProductsPublic(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findAll(pageable);

        return PageResponse.<PublicProductResponse>builder()
                .content(productPage.getContent().stream()
                        .map(this::mapToPublicProductResponse)
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
     * Lấy product theo ID (ADMIN - đầy đủ thông tin)
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

    /**
     * Tìm kiếm & lọc sản phẩm (PUBLIC – ẩn soldCount, sourceUrl)
     */
    public PageResponse<PublicProductResponse> searchProductsPublic(ProductSearchRequest request, int page, int size) {
        Query query = buildProductQuery(request);

        long total = mongoTemplate.count(query, Product.class);

        Sort sort = buildSort(request.getSortBy(), request.getSortDir());
        query.with(sort).skip((long) page * size).limit(size);

        List<Product> products = mongoTemplate.find(query, Product.class);
        List<PublicProductResponse> content = products.stream()
                .map(this::mapToPublicProductResponse)
                .toList();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PublicProductResponse> pageResult = new PageImpl<>(content, pageable, total);

        return PageResponse.<PublicProductResponse>builder()
                .content(content)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .first(pageResult.isFirst())
                .last(pageResult.isLast())
                .build();
    }

    /**
     * Tìm kiếm & lọc sản phẩm (ADMIN – đầy đủ thông tin)
     */
    public PageResponse<ProductResponse> searchProductsAdmin(ProductSearchRequest request, int page, int size) {
        SecurityUtils.checkAdminRole();
        Query query = buildProductQuery(request);

        long total = mongoTemplate.count(query, Product.class);
        Sort sort = buildSort(request.getSortBy(), request.getSortDir());
        query.with(sort).skip((long) page * size).limit(size);

        List<Product> products = mongoTemplate.find(query, Product.class);
        List<ProductResponse> content = products.stream()
                .map(this::mapToProductResponse)
                .toList();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> pageResult = new PageImpl<>(content, pageable, total);

        return PageResponse.<ProductResponse>builder()
                .content(content)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .first(pageResult.isFirst())
                .last(pageResult.isLast())
                .build();
    }

    // ===== Search helpers =====

    private Query buildProductQuery(ProductSearchRequest request) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (request.getQuery() != null && !request.getQuery().isBlank()) {
            Pattern pattern = Pattern.compile(request.getQuery().trim(), Pattern.CASE_INSENSITIVE);
            criteriaList.add(Criteria.where("name").regex(pattern));
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            criteriaList.add(Criteria.where("category").is(request.getCategory().trim()));
        }
        if (request.getMinPrice() != null) {
            criteriaList.add(Criteria.where("price").gte(request.getMinPrice()));
        }
        if (request.getMaxPrice() != null) {
            criteriaList.add(Criteria.where("price").lte(request.getMaxPrice()));
        }
        if (request.getInStock() != null) {
            criteriaList.add(Criteria.where("inStock").is(request.getInStock()));
        }

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        return query;
    }

    private Sort buildSort(String sortBy, String sortDir) {
        String field = (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
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
                .soldCount(product.getSoldCount() != null ? product.getSoldCount() : 0)
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

    /**
     * Map sang PublicProductResponse dành cho USER
     * Ẩn: soldCount, sourceUrl, sourceProvider, createdAt, updatedAt
     */
    private PublicProductResponse mapToPublicProductResponse(Product product) {
        PublicProductResponse response = PublicProductResponse.builder()
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
                .color(product.getColor() != null
                        ? PublicProductResponse.ColorResponse.builder()
                                .name(product.getColor().getName())
                                .hex(product.getColor().getHex())
                                .build()
                        : null)
                .styles(product.getStyles())
                .origin(product.getOrigin())
                .dimensions(product.getDimensions() != null
                        ? PublicProductResponse.DimensionsResponse.builder()
                                .width(product.getDimensions().getWidth())
                                .height(product.getDimensions().getHeight())
                                .depth(product.getDimensions().getDepth())
                                .unit(product.getDimensions().getUnit())
                                .build()
                        : null)
                .dimensionsRaw(product.getDimensionsRaw())
                .description(product.getDescription())
                .careInstructions(product.getCareInstructions())
                .notes(product.getNotes())
                .images(product.getImages())
                .soldCount(product.getSoldCount() != null ? product.getSoldCount() : 0)
                .avgRating(0.0) // Placeholder - logic added below
                .reviewCount(0)
                .build();

        List<cap2.schema.Review> reviews = reviewRepository.findByProductId(product.getId(), org.springframework.data.domain.Pageable.unpaged()).getContent();
        if (!reviews.isEmpty()) {
            double avg = reviews.stream().mapToInt(cap2.schema.Review::getRating).average().orElse(0.0);
            response.setAvgRating(Math.round(avg * 10.0) / 10.0);
            response.setReviewCount(reviews.size());
        }

        return response;
    }
}
