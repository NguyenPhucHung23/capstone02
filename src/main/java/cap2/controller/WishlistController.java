package cap2.controller;

import cap2.dto.response.ApiResponse;
import cap2.dto.response.WishlistResponse;
import cap2.service.WishlistService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WishlistController {

    WishlistService wishlistService;

    /**
     * Lấy wishlist của mình (USER)
     * GET /wishlist
     */
    @GetMapping
    public ApiResponse<WishlistResponse> getMyWishlist() {
        return ApiResponse.ok("Lấy danh sách yêu thích thành công", wishlistService.getMyWishlist());
    }

    /**
     * Thêm sản phẩm vào wishlist (USER)
     * POST /wishlist/{productId}
     */
    @PostMapping("/{productId}")
    public ApiResponse<WishlistResponse> addToWishlist(@PathVariable String productId) {
        return ApiResponse.ok("Đã thêm vào danh sách yêu thích", wishlistService.addToWishlist(productId));
    }

    /**
     * Xóa sản phẩm khỏi wishlist (USER)
     * DELETE /wishlist/{productId}
     */
    @DeleteMapping("/{productId}")
    public ApiResponse<WishlistResponse> removeFromWishlist(@PathVariable String productId) {
        return ApiResponse.ok("Đã xóa khỏi danh sách yêu thích", wishlistService.removeFromWishlist(productId));
    }

    /**
     * Kiểm tra sản phẩm có trong wishlist không (USER)
     * GET /wishlist/{productId}/check
     */
    @GetMapping("/{productId}/check")
    public ApiResponse<Map<String, Boolean>> checkInWishlist(@PathVariable String productId) {
        boolean inWishlist = wishlistService.isInWishlist(productId);
        return ApiResponse.ok("Kiểm tra thành công", Map.of("inWishlist", inWishlist));
    }
}
