package cap2.controller;

import cap2.dto.request.AddToCartRequest;
import cap2.dto.request.UpdateCartItemRequest;
import cap2.dto.response.ApiResponse;
import cap2.dto.response.CartResponse;
import cap2.service.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService;

    /**
     * Lấy giỏ hàng của user hiện tại
     */
    @GetMapping
    public ApiResponse<CartResponse> getMyCart() {
        CartResponse response = cartService.getMyCart();
        return ApiResponse.ok("Get cart successfully", response);
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    @PostMapping("/items")
    public ApiResponse<CartResponse> addToCart(@Valid @RequestBody AddToCartRequest request) {
        CartResponse response = cartService.addToCart(request);
        return ApiResponse.ok("Added to cart successfully", response);
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     */
    @PutMapping("/items/{productId}")
    public ApiResponse<CartResponse> updateCartItem(
            @PathVariable String productId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartResponse response = cartService.updateCartItem(productId, request);
        return ApiResponse.ok("Cart item updated successfully", response);
    }

    /**
     * Xóa một sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/items/{productId}")
    public ApiResponse<CartResponse> removeFromCart(@PathVariable String productId) {
        CartResponse response = cartService.removeFromCart(productId);
        return ApiResponse.ok("Removed from cart successfully", response);
    }
}
