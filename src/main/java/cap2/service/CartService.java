package cap2.service;

import cap2.dto.request.AddToCartRequest;
import cap2.dto.request.UpdateCartItemRequest;
import cap2.dto.response.CartResponse;
import cap2.exception.AppException;
import cap2.exception.ErrorCode;
import cap2.repository.CartRepository;
import cap2.repository.ProductRepository;
import cap2.schema.Cart;
import cap2.schema.Product;
import cap2.util.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    CartRepository cartRepository;
    ProductRepository productRepository;

    /**
     * Lấy giỏ hàng của user hiện tại
     */
    public CartResponse getMyCart() {
        String userId = SecurityUtils.getCurrentUserId();
        Cart cart = getOrCreateCart(userId);
        return mapToCartResponse(cart);
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    public CartResponse addToCart(AddToCartRequest request) {
        String userId = SecurityUtils.getCurrentUserId();

        // Kiểm tra product tồn tại
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Cart cart = getOrCreateCart(userId);

        // Kiểm tra xem product đã có trong giỏ hàng chưa
        Optional<Cart.CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Nếu đã có → tăng số lượng
            int newQuantity = existingItem.get().getQuantity() + request.getQuantity();
            if (newQuantity > 100) {
                throw new AppException(ErrorCode.INVALID_INPUT);
            }
            existingItem.get().setQuantity(newQuantity);
            log.info("Updated quantity for product {} in cart of user {}", request.getProductId(), userId);
        } else {
            // Nếu chưa có → thêm mới
            Cart.CartItem newItem = Cart.CartItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productImage(product.getImages() != null && !product.getImages().isEmpty()
                            ? product.getImages().getFirst() : null)
                    .price(product.getPrice())
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
            log.info("Added product {} to cart of user {}", request.getProductId(), userId);
        }

        cart.setUpdatedAt(Instant.now());
        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     */
    public CartResponse updateCartItem(String productId, UpdateCartItemRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        // Tìm item trong giỏ hàng
        Cart.CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        // Cập nhật số lượng
        cartItem.setQuantity(request.getQuantity());
        cart.setUpdatedAt(Instant.now());

        Cart savedCart = cartRepository.save(cart);
        log.info("Updated cart item quantity for product {} to {} for user {}",
                productId, request.getQuantity(), userId);

        return mapToCartResponse(savedCart);
    }

    /**
     * Xóa một sản phẩm khỏi giỏ hàng
     */
    public CartResponse removeFromCart(String productId) {
        String userId = SecurityUtils.getCurrentUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        if (!removed) {
            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        cart.setUpdatedAt(Instant.now());
        Cart savedCart = cartRepository.save(cart);
        log.info("Removed product {} from cart of user {}", productId, userId);

        return mapToCartResponse(savedCart);
    }


    // ===== Helper methods =====

    /**
     * Lấy hoặc tạo mới giỏ hàng cho user
     */
    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(userId)
                            .items(new ArrayList<>())
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build();
                    log.info("Created new cart for user {}", userId);
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Map Cart entity sang CartResponse DTO
     */
    private CartResponse mapToCartResponse(Cart cart) {
        List<CartResponse.CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> CartResponse.CartItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productImage(item.getProductImage())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getPrice() * item.getQuantity())
                        .build())
                .toList();

        int totalItems = cart.getItems().stream()
                .mapToInt(Cart.CartItem::getQuantity)
                .sum();

        double totalPrice = itemResponses.stream()
                .mapToDouble(CartResponse.CartItemResponse::getSubtotal)
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(itemResponses)
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}
