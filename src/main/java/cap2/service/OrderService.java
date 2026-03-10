package cap2.service;

import cap2.dto.request.CreateOrderRequest;
import cap2.dto.request.UpdateOrderStatusRequest;
import cap2.dto.response.OrderResponse;
import cap2.dto.response.PageResponse;
import cap2.exception.AppException;
import cap2.exception.ErrorCode;
import cap2.repository.CartRepository;
import cap2.repository.OrderRepository;
import cap2.repository.ProfileRepository;
import cap2.repository.UserRepository;
import cap2.repository.ProductRepository;
import cap2.schema.Cart;
import cap2.schema.Order;
import cap2.schema.Profile;
import cap2.schema.User;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    CartRepository cartRepository;
    UserRepository userRepository;
    ProfileRepository profileRepository;
    ProductRepository productRepository;

    public OrderResponse createOrder(CreateOrderRequest request) {
        String userId = SecurityUtils.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));


        // Profile không bắt buộc - lấy nếu có
        Profile profile = profileRepository.findByUserId(userId).orElse(null);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        List<Order.OrderItem> orderItems = cart.getItems().stream()
                .map(item -> Order.OrderItem.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productImage(item.getProductImage())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getPrice() * item.getQuantity())
                        .build())
                .toList();

        double subtotal = orderItems.stream()
                .mapToDouble(Order.OrderItem::getSubtotal)
                .sum();

        double shippingFee = calculateShippingFee(subtotal);
        double discount = calculateDiscount(request.getDiscountCode(), subtotal);
        double totalAmount = subtotal + shippingFee - discount;

        Order.PaymentMethod paymentMethod = parsePaymentMethod(request.getPaymentMethod());

        // Ưu tiên dùng thông tin từ request, nếu không có thì lấy từ Profile (nếu có profile)
        String customerName = hasValue(request.getCustomerName()) ? request.getCustomerName() :
                              (profile != null ? profile.getFullName() : null);
        String customerPhone = hasValue(request.getCustomerPhone()) ? request.getCustomerPhone() :
                               (profile != null ? profile.getPhone() : null);
        String shippingAddress = hasValue(request.getShippingAddress()) ? request.getShippingAddress() :
                                 (profile != null ? profile.getAddress() : null);

        // Xác định city/province: ưu tiên request, fallback từ profile
        // city và province không được tồn tại cùng lúc
        String shippingCity;
        String shippingProvince;
        if (hasValue(request.getShippingCity())) {
            shippingCity = request.getShippingCity();
            shippingProvince = null;
        } else if (hasValue(request.getShippingProvince())) {
            shippingProvince = request.getShippingProvince();
            shippingCity = null;
        } else if (profile != null) {
            shippingCity = profile.getCity();
            shippingProvince = profile.getProvince();
        } else {
            shippingCity = null;
            shippingProvince = null;
        }

        String shippingWard = hasValue(request.getShippingWard()) ? request.getShippingWard() :
                              (profile != null ? profile.getWard() : null);

        // Validate thông tin giao hàng bắt buộc
        validateShippingInfo(customerName, customerPhone, shippingAddress, shippingCity,
                           shippingProvince, shippingWard);

        Order order = Order.builder()
                .orderCode(generateOrderCode())
                .userId(userId)
                .customerName(customerName)
                .customerEmail(user.getEmail())
                .customerPhone(customerPhone)
                .shippingAddress(shippingAddress)
                .shippingCity(shippingCity)
                .shippingProvince(shippingProvince)
                .shippingWard(shippingWard)
                .items(orderItems)
                .subtotal(subtotal)
                .shippingFee(shippingFee)
                .discount(discount)
                .discountCode(request.getDiscountCode())
                .totalAmount(totalAmount)
                .paymentMethod(paymentMethod)
                .paymentStatus(Order.PaymentStatus.PENDING)
                .status(Order.OrderStatus.PENDING)
                .note(request.getNote())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        // Cập nhật soldCount cho từng sản phẩm
        for (Order.OrderItem item : orderItems) {
            productRepository.findById(item.getProductId()).ifPresent(product -> {
                int current = product.getSoldCount() != null ? product.getSoldCount() : 0;
                product.setSoldCount(current + item.getQuantity());
                productRepository.save(product);
            });
        }

        cart.getItems().clear();
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        log.info("Đơn hàng {} được tạo bởi user {}", savedOrder.getOrderCode(), userId);
        return mapToOrderResponse(savedOrder);
    }

    public PageResponse<OrderResponse> getMyOrders(int page, int size) {
        String userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);
        return buildPageResponse(orderPage);
    }

    public OrderResponse getOrderById(String orderId) {
        String userId = SecurityUtils.getCurrentUserId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId) && !SecurityUtils.isAdmin()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return mapToOrderResponse(order);
    }

    public OrderResponse getOrderByCode(String orderCode) {
        String userId = SecurityUtils.getCurrentUserId();
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId) && !SecurityUtils.isAdmin()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return mapToOrderResponse(order);
    }

    public OrderResponse cancelOrder(String orderId) {
        String userId = SecurityUtils.getCurrentUserId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // User chỉ hủy được đơn của mình, Admin hủy được tất cả
        if (!order.getUserId().equals(userId) && !SecurityUtils.isAdmin()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // User chỉ hủy được đơn PENDING, Admin hủy được cả PENDING và CONFIRMED
        if (!SecurityUtils.isAdmin() && order.getStatus() != Order.OrderStatus.PENDING) {
            throw new AppException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        if (SecurityUtils.isAdmin() &&
            (order.getStatus() == Order.OrderStatus.SHIPPING ||
             order.getStatus() == Order.OrderStatus.DELIVERED ||
             order.getStatus() == Order.OrderStatus.CANCELLED)) {
            throw new AppException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setUpdatedAt(Instant.now());
        Order savedOrder = orderRepository.save(order);

        log.info("Đơn hàng {} đã bị hủy bởi {}", order.getOrderCode(),
                SecurityUtils.isAdmin() ? "admin" : "user " + userId);
        return mapToOrderResponse(savedOrder);
    }

    // ===== ADMIN APIs =====

    public PageResponse<OrderResponse> getAllOrders(int page, int size) {
        SecurityUtils.checkAdminRole();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return buildPageResponse(orderPage);
    }

    public PageResponse<OrderResponse> getOrdersByStatus(String status, int page, int size) {
        SecurityUtils.checkAdminRole();
        Order.OrderStatus orderStatus = parseOrderStatus(status);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orderPage = orderRepository.findByStatus(orderStatus, pageable);
        return buildPageResponse(orderPage);
    }

    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        SecurityUtils.checkAdminRole();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        Order.OrderStatus newStatus = parseOrderStatus(request.getStatus());

        // Kiểm tra đơn hàng đã hủy
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new AppException(ErrorCode.ORDER_ALREADY_CANCELLED);
        }

        // Kiểm tra đơn hàng đã giao
        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new AppException(ErrorCode.ORDER_ALREADY_DELIVERED);
        }

        // Xử lý hủy đơn hàng - chỉ cho phép hủy khi đang PENDING hoặc CONFIRMED
        if (newStatus == Order.OrderStatus.CANCELLED) {
            if (order.getStatus() != Order.OrderStatus.PENDING &&
                order.getStatus() != Order.OrderStatus.CONFIRMED) {
                throw new AppException(ErrorCode.ORDER_CANNOT_CANCEL);
            }
            order.setStatus(Order.OrderStatus.CANCELLED);
            order.setUpdatedAt(Instant.now());
            Order savedOrder = orderRepository.save(order);
            log.info("Admin hủy đơn hàng {}", order.getOrderCode());
            return mapToOrderResponse(savedOrder);
        }

        order.setStatus(newStatus);

        // Tự động cập nhật thanh toán khi giao hàng COD
        if (newStatus == Order.OrderStatus.DELIVERED &&
            order.getPaymentMethod() == Order.PaymentMethod.COD) {
            order.setPaymentStatus(Order.PaymentStatus.PAID);
        }

        order.setUpdatedAt(Instant.now());
        Order savedOrder = orderRepository.save(order);

        log.info("Admin cập nhật trạng thái đơn hàng {} thành {}", order.getOrderCode(), newStatus);
        return mapToOrderResponse(savedOrder);
    }

    // ===== Helper methods =====

    private void validateShippingInfo(String customerName, String customerPhone,
                                     String shippingAddress, String shippingCity,
                                     String shippingProvince, String shippingWard) {
        if (!hasValue(customerName) || !hasValue(customerPhone) ||
            !hasValue(shippingAddress) || !hasValue(shippingWard)) {
            throw new AppException(ErrorCode.MISSING_SHIPPING_INFO);
        }
        // Phải có ít nhất 1 trong 2: city hoặc province
        if (!hasValue(shippingCity) && !hasValue(shippingProvince)) {
            throw new AppException(ErrorCode.MISSING_SHIPPING_INFO);
        }
        // Không được tồn tại cả 2 cùng lúc
        if (hasValue(shippingCity) && hasValue(shippingProvince)) {
            throw new AppException(ErrorCode.INVALID_SHIPPING_ADDRESS);
        }
    }

    private boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }

    private String generateOrderCode() {
        // Sử dụng timestamp + random + kiểm tra unique
        String orderCode;
        int maxAttempts = 10;
        int attempts = 0;

        do {
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            long nanoTime = System.nanoTime() % 1000000; // 6 chữ số cuối của nano time
            int random = (int) (Math.random() * 10000); // Random 0-9999
            orderCode = String.format("ORD%s%06d%04d", date, nanoTime, random);
            attempts++;
        } while (orderRepository.existsByOrderCode(orderCode) && attempts < maxAttempts);

        if (attempts >= maxAttempts) {
            // Fallback: dùng UUID nếu không tạo được mã unique sau nhiều lần thử
            orderCode = "ORD" + java.util.UUID.randomUUID().toString().substring(0, 16).toUpperCase();
        }

        return orderCode;
    }

    private double calculateShippingFee(double subtotal) {
        if (subtotal >= 10000000) {
            return 0;
        }
        return 500000;
    }

    private double calculateDiscount(String discountCode, double subtotal) {
        if (discountCode == null || discountCode.isBlank()) {
            return 0;
        }

        return switch (discountCode.toUpperCase()) {
            case "FREESHIP" -> 500000;
            case "SALE10" -> subtotal * 0.1;
            case "SALE20" -> subtotal * 0.2;
            default -> 0;
        };
    }

    private Order.PaymentMethod parsePaymentMethod(String method) {
        try {
            return Order.PaymentMethod.valueOf(method.toUpperCase());
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }
    }

    private Order.OrderStatus parseOrderStatus(String status) {
        try {
            return Order.OrderStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }
    }

    private PageResponse<OrderResponse> buildPageResponse(Page<Order> orderPage) {
        return PageResponse.<OrderResponse>builder()
                .content(orderPage.getContent().stream()
                        .map(this::mapToOrderResponse)
                        .toList())
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .first(orderPage.isFirst())
                .last(orderPage.isLast())
                .build();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productImage(item.getProductImage())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        int totalItems = order.getItems().stream()
                .mapToInt(Order.OrderItem::getQuantity)
                .sum();

        // Xây dựng địa chỉ đầy đủ: dùng city nếu có, ngược lại dùng province
        String locationName = hasValue(order.getShippingCity()) ? order.getShippingCity() : order.getShippingProvince();
        String fullAddress = String.format("%s, %s, %s",
                order.getShippingAddress(),
                order.getShippingWard(),
                locationName);

        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUserId())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .shippingAddress(order.getShippingAddress())
                .shippingCity(order.getShippingCity())
                .shippingProvince(order.getShippingProvince())
                .shippingWard(order.getShippingWard())
                .fullShippingAddress(fullAddress)
                .items(itemResponses)
                .totalItems(totalItems)
                .subtotal(order.getSubtotal())
                .shippingFee(order.getShippingFee())
                .discount(order.getDiscount())
                .discountCode(order.getDiscountCode())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod().name())
                .paymentStatus(order.getPaymentStatus().name())
                .status(order.getStatus().name())
                .statusDisplay(getStatusDisplay(order.getStatus()))
                .paymentMethodDisplay(getPaymentMethodDisplay(order.getPaymentMethod()))
                .paymentStatusDisplay(getPaymentStatusDisplay(order.getPaymentStatus()))
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private String getStatusDisplay(Order.OrderStatus status) {
        return switch (status) {
            case PENDING -> "Đang xử lý";
            case CONFIRMED -> "Đã xác nhận";
            case SHIPPING -> "Đang giao";
            case DELIVERED -> "Đã giao";
            case CANCELLED -> "Đã hủy";
        };
    }

    private String getPaymentMethodDisplay(Order.PaymentMethod method) {
        return switch (method) {
            case COD -> "COD";
            case VNPAY -> "VNPay";
            case MOMO -> "Momo";
        };
    }

    private String getPaymentStatusDisplay(Order.PaymentStatus status) {
        return switch (status) {
            case PENDING -> "Chờ thanh toán";
            case PAID -> "Đã thanh toán";
            case FAILED -> "Thanh toán thất bại";
        };
    }
}
