package cap2.service;

import cap2.dto.request.ReviewRequest;
import cap2.dto.response.PageResponse;
import cap2.dto.response.ReviewResponse;
import cap2.exception.AppException;
import cap2.exception.ErrorCode;
import cap2.repository.OrderRepository;
import cap2.repository.ProductRepository;
import cap2.repository.ProfileRepository;
import cap2.repository.ReviewRepository;
import cap2.schema.Order;
import cap2.schema.Profile;
import cap2.schema.Review;
import cap2.schema.UserBehaviorEvent;
import cap2.util.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {

    ReviewRepository reviewRepository;
    OrderRepository orderRepository;
    ProductRepository productRepository;
    ProfileRepository profileRepository;
    BehaviorService behaviorService;

    public ReviewResponse createReview(ReviewRequest request) {
        String userId = SecurityUtils.getCurrentUserId();

        productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Order order = orderRepository.findByOrderCode(request.getOrderCode())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() != Order.OrderStatus.DELIVERED) {
            throw new AppException(ErrorCode.ORDER_NOT_DELIVERED);
        }

        if (!order.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        boolean productInOrder = order.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(request.getProductId()));
        if (!productInOrder) {
            throw new AppException(ErrorCode.REVIEW_NOT_YOUR_ORDER);
        }

        if (reviewRepository.existsByUserIdAndProductId(userId, request.getProductId())) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Review review = Review.builder()
                .productId(request.getProductId())
                .userId(userId)
                .orderCode(request.getOrderCode())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);

        behaviorService.saveEventSafely(
            userId,
            request.getProductId(),
            UserBehaviorEvent.EventType.RATING,
            request.getRating(),
            null,
            null
        );

        log.info("User {} đã đánh giá sản phẩm {}", userId, request.getProductId());
        return mapToResponse(saved);
    }

    public PageResponse<ReviewResponse> getProductReviews(String productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviewPage = reviewRepository.findByProductId(productId, pageable);
        return buildPageResponse(reviewPage);
    }

    public ReviewResponse.RatingSummary getProductRatingSummary(String productId) {
        Pageable all = PageRequest.of(0, Integer.MAX_VALUE);
        List<Review> reviews = reviewRepository.findByProductId(productId, all).getContent();

        long total = reviews.size();
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        avg = Math.round(avg * 10.0) / 10.0;

        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int star = i;
            distribution.put(star, reviews.stream().filter(r -> r.getRating() == star).count());
        }

        return ReviewResponse.RatingSummary.builder()
                .productId(productId)
                .avgRating(avg)
                .reviewCount(total)
                .distribution(distribution)
                .build();
    }

    public PageResponse<ReviewResponse> getMyReviews(int page, int size) {
        String userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviewPage = reviewRepository.findByUserId(userId, pageable);
        return buildPageResponse(reviewPage);
    }

    public void deleteReview(String reviewId) {
        String userId = SecurityUtils.getCurrentUserId();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        if (!SecurityUtils.isAdmin() && !review.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        reviewRepository.delete(review);
        log.info("Review {} đã bị xóa", reviewId);
    }

    private ReviewResponse mapToResponse(Review review) {
        String reviewerName = profileRepository.findByUserId(review.getUserId())
                .map(Profile::getFullName)
                .orElse("Người dùng ẩn danh");

        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProductId())
                .userId(review.getUserId())
                .reviewerName(reviewerName)
                .orderCode(review.getOrderCode())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    private PageResponse<ReviewResponse> buildPageResponse(Page<Review> page) {
        return PageResponse.<ReviewResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
