package cap2.controller;

import cap2.dto.request.ReviewRequest;
import cap2.dto.response.ApiResponse;
import cap2.dto.response.PageResponse;
import cap2.dto.response.ReviewResponse;
import cap2.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {

    ReviewService reviewService;

    /**
     * Tạo đánh giá sản phẩm (USER – đã đăng nhập, đơn hàng phải DELIVERED)
     * POST /reviews
     */
    @PostMapping
    public ApiResponse<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        return ApiResponse.ok("Đánh giá sản phẩm thành công", reviewService.createReview(request));
    }

    /**
     * Xem danh sách đánh giá của 1 sản phẩm (PUBLIC)
     * GET /reviews/product/{productId}?page=0&size=10
     */
    @GetMapping("/product/{productId}")
    public ApiResponse<PageResponse<ReviewResponse>> getProductReviews(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Lấy đánh giá sản phẩm thành công",
                reviewService.getProductReviews(productId, page, size));
    }

    @GetMapping("/all")
    public ApiResponse<PageResponse<ReviewResponse>> getAllReview(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Lấy tất cả đánh giá thành công",
                reviewService.getAllReview(page, size));
    }

    /**
     * Tổng hợp đánh giá của 1 sản phẩm (avgRating, reviewCount, phân bố sao) (PUBLIC)
     * GET /reviews/product/{productId}/summary
     */
    @GetMapping("/product/{productId}/summary")
    public ApiResponse<ReviewResponse.RatingSummary> getProductRatingSummary(
            @PathVariable String productId) {
        return ApiResponse.ok("Lấy tổng hợp đánh giá thành công",
                reviewService.getProductRatingSummary(productId));
    }

    /**
     * Xem đánh giá của mình (USER)
     * GET /reviews/my?page=0&size=10
     */
    @GetMapping("/my")
    public ApiResponse<PageResponse<ReviewResponse>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Lấy đánh giá của bạn thành công",
                reviewService.getMyReviews(page, size));
    }

    /**
     * Xóa đánh giá (User xóa của mình / Admin xóa bất kỳ)
     * DELETE /reviews/{id}
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        return ApiResponse.ok("Xóa đánh giá thành công", null);
    }
}
