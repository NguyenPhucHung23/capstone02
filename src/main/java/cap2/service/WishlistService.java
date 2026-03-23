package cap2.service;

import cap2.dto.response.PublicProductResponse;
import cap2.dto.response.WishlistResponse;
import cap2.exception.AppException;
import cap2.exception.ErrorCode;
import cap2.repository.ProductRepository;
import cap2.repository.WishlistRepository;
import cap2.schema.Product;
import cap2.schema.Wishlist;
import cap2.util.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WishlistService {

    WishlistRepository wishlistRepository;
    ProductRepository productRepository;

    /**
     * Lấy wishlist của user hiện tại (tự tạo nếu chưa có)
     */
    public WishlistResponse getMyWishlist() {
        String userId = SecurityUtils.getCurrentUserId();
        Wishlist wishlist = getOrCreateWishlist(userId);
        return buildResponse(wishlist);
    }

    /**
     * Thêm sản phẩm vào wishlist
     */
    public WishlistResponse addToWishlist(String productId) {
        String userId = SecurityUtils.getCurrentUserId();

        // Kiểm tra sản phẩm tồn tại
        productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Wishlist wishlist = getOrCreateWishlist(userId);

        if (wishlist.getProductIds().contains(productId)) {
            throw new AppException(ErrorCode.WISHLIST_ITEM_ALREADY_EXISTS);
        }

        wishlist.getProductIds().add(productId);
        wishlist.setUpdatedAt(Instant.now());
        wishlistRepository.save(wishlist);

        log.info("User {} đã thêm sản phẩm {} vào wishlist", userId, productId);
        return buildResponse(wishlist);
    }

    /**
     * Xóa sản phẩm khỏi wishlist
     */
    public WishlistResponse removeFromWishlist(String productId) {
        String userId = SecurityUtils.getCurrentUserId();
        Wishlist wishlist = getOrCreateWishlist(userId);

        if (!wishlist.getProductIds().remove(productId)) {
            throw new AppException(ErrorCode.WISHLIST_ITEM_NOT_FOUND);
        }

        wishlist.setUpdatedAt(Instant.now());
        wishlistRepository.save(wishlist);

        log.info("User {} đã xóa sản phẩm {} khỏi wishlist", userId, productId);
        return buildResponse(wishlist);
    }

    /**
     * Kiểm tra sản phẩm có trong wishlist không
     */
    public boolean isInWishlist(String productId) {
        String userId = SecurityUtils.getCurrentUserId();
        Wishlist wishlist = getOrCreateWishlist(userId);
        return wishlist.getProductIds().contains(productId);
    }

    // ===== Helpers =====

    private Wishlist getOrCreateWishlist(String userId) {
        return wishlistRepository.findByUserId(userId).orElseGet(() -> {
            Wishlist newWishlist = Wishlist.builder()
                    .userId(userId)
                    .productIds(new ArrayList<>())
                    .updatedAt(Instant.now())
                    .build();
            log.info("Tạo wishlist mới cho user {}", userId);
            return wishlistRepository.save(newWishlist);
        });
    }

    private WishlistResponse buildResponse(Wishlist wishlist) {
        List<PublicProductResponse> products = wishlist.getProductIds().stream()
                .map(productId -> productRepository.findById(productId).orElse(null))
                .filter(product -> product != null)
                .map(this::mapProduct)
                .toList();

        return WishlistResponse.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUserId())
                .products(products)
                .totalItems(products.size())
                .build();
    }

    private PublicProductResponse mapProduct(Product product) {
        String image = (product.getImages() != null && !product.getImages().isEmpty())
                ? product.getImages().getFirst() : null;
        return PublicProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .category(product.getCategory())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .priceFormatted(product.getPriceFormatted())
                .inStock(product.getInStock())
                .stock(product.getStock())
                .images(product.getImages())
                .build();
    }
}
