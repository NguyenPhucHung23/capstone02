package cap2.service;

import cap2.dto.request.AdminOrderFilterRequest;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    MongoTemplate mongoTemplate;
    EmailService emailService;

    public OrderResponse createOrder(CreateOrderRequest request) {
        String userId = SecurityUtils.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));


        Profile profile = profileRepository.findByUserId(userId).orElse(null);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        List<Order.OrderItem> orderItems = new java.util.ArrayList<>();
        for (cap2.schema.Cart.CartItem item : cart.getItems()) {
            cap2.schema.Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new AppException(cap2.exception.ErrorCode.PRODUCT_NOT_FOUND));

            if (product.getStock() != null) {
                if (product.getStock() < item.getQuantity()) {
                    throw new AppException(cap2.exception.ErrorCode.OUT_OF_STOCK);
                }
                product.setStock(product.getStock() - item.getQuantity());
                product.setInStock(product.getStock() > 0);
                productRepository.save(product);
            }

            orderItems.add(Order.OrderItem.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .productImage(item.getProductImage())
                    .price(item.getPrice())
                    .quantity(item.getQuantity())
                    .subtotal(item.getPrice() * item.getQuantity())
                    .build());
        }

        double subtotal = orderItems.stream()
                .mapToDouble(Order.OrderItem::getSubtotal)
                .sum();

        double shippingFee = calculateShippingFee(subtotal);
        double discount = calculateDiscount(request.getDiscountCode(), subtotal);
        double totalAmount = subtotal + shippingFee - discount;

        Order.PaymentMethod paymentMethod = parsePaymentMethod(request.getPaymentMethod());

        String customerName = hasValue(request.getCustomerName()) ? request.getCustomerName() :
                               (profile != null ? profile.getFullName() : null);
        String customerPhone = hasValue(request.getCustomerPhone()) ? request.getCustomerPhone() :
                                (profile != null ? profile.getPhone() : null);
        String shippingAddress = hasValue(request.getShippingAddress()) ? request.getShippingAddress() :
                                  (profile != null ? profile.getAddress() : null);

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

        cart.getItems().clear();
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        log.info("Đơn hàng {} được tạo bởi user {}", savedOrder.getOrderCode(), userId);

        emailService.sendOrderConfirmation(savedOrder);

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

        if (!order.getUserId().equals(userId) && !SecurityUtils.isAdmin()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

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
        restoreInventory(savedOrder);

        emailService.sendOrderStatusUpdate(savedOrder);

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

    /**
     * Tìm kiếm và lọc đơn hàng (Admin)
     */
    public PageResponse<OrderResponse> searchOrders(AdminOrderFilterRequest request, int page, int size) {
        SecurityUtils.checkAdminRole();

        List<Criteria> criteriaList = new ArrayList<>();

        if (hasValue(request.getKeyword())) {
            String kw = request.getKeyword().trim();
            Criteria kwCriteria = new Criteria().orOperator(
                    Criteria.where("orderCode").regex(kw, "i"),
                    Criteria.where("customerName").regex(kw, "i"),
                    Criteria.where("customerEmail").regex(kw, "i")
            );
            criteriaList.add(kwCriteria);
        }

        if (hasValue(request.getOrderCode())) {
            criteriaList.add(Criteria.where("orderCode").regex(request.getOrderCode().trim(), "i"));
        }

        if (hasValue(request.getCustomerName())) {
            criteriaList.add(Criteria.where("customerName").regex(request.getCustomerName().trim(), "i"));
        }

        if (hasValue(request.getCustomerEmail())) {
            criteriaList.add(Criteria.where("customerEmail").regex(request.getCustomerEmail().trim(), "i"));
        }

        if (hasValue(request.getStatus())) {
            criteriaList.add(Criteria.where("status").is(request.getStatus().toUpperCase()));
        }

        if (hasValue(request.getPaymentMethod())) {
            criteriaList.add(Criteria.where("paymentMethod").is(request.getPaymentMethod().toUpperCase()));
        }

        if (hasValue(request.getPaymentStatus())) {
            criteriaList.add(Criteria.where("paymentStatus").is(request.getPaymentStatus().toUpperCase()));
        }

        if (request.getFromDate() != null) {
            Instant from = request.getFromDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
            criteriaList.add(Criteria.where("createdAt").gte(from));
        }

        if (request.getToDate() != null) {
            Instant to = request.getToDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
            criteriaList.add(Criteria.where("createdAt").lt(to));
        }

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, Order.class);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        query.with(sort).skip((long) page * size).limit(size);

        List<Order> orders = mongoTemplate.find(query, Order.class);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orderPage = new org.springframework.data.domain.PageImpl<>(orders, pageable, total);

        return buildPageResponse(orderPage);
    }

    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        SecurityUtils.checkAdminRole();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        boolean wasPaid = order.getPaymentStatus() == Order.PaymentStatus.PAID;
        Order.OrderStatus newStatus = parseOrderStatus(request.getStatus());

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new AppException(ErrorCode.ORDER_ALREADY_CANCELLED);
        }

        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new AppException(ErrorCode.ORDER_ALREADY_DELIVERED);
        }

        if (newStatus == Order.OrderStatus.CANCELLED) {
            if (order.getStatus() != Order.OrderStatus.PENDING &&
                order.getStatus() != Order.OrderStatus.CONFIRMED) {
                throw new AppException(ErrorCode.ORDER_CANNOT_CANCEL);
            }
            order.setStatus(Order.OrderStatus.CANCELLED);
            order.setUpdatedAt(Instant.now());
            Order savedOrder = orderRepository.save(order);
            restoreInventory(savedOrder);

            log.info("Admin hủy đơn hàng {}", order.getOrderCode());
            return mapToOrderResponse(savedOrder);
        }

        order.setStatus(newStatus);

        if (newStatus == Order.OrderStatus.DELIVERED &&
            order.getPaymentMethod() == Order.PaymentMethod.COD) {
            order.setPaymentStatus(Order.PaymentStatus.PAID);
        }

        order.setUpdatedAt(Instant.now());
        Order savedOrder = orderRepository.save(order);

        if (!wasPaid && savedOrder.getPaymentStatus() == Order.PaymentStatus.PAID) {
            applyInventoryOnPaidOrder(savedOrder);
        }

        emailService.sendOrderStatusUpdate(savedOrder);

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
        if (!hasValue(shippingCity) && !hasValue(shippingProvince)) {
            throw new AppException(ErrorCode.MISSING_SHIPPING_INFO);
        }
        if (hasValue(shippingCity) && hasValue(shippingProvince)) {
            throw new AppException(ErrorCode.INVALID_SHIPPING_ADDRESS);
        }
    }

    private boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }

    private String generateOrderCode() {
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

    public void applyInventoryOnPaidOrder(Order order) {
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            return;
        }

        for (Order.OrderItem item : order.getItems()) {
            if (item == null || item.getProductId() == null) {
                continue;
            }

            int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
            if (quantity <= 0) {
                continue;
            }

            productRepository.findById(item.getProductId()).ifPresent(product -> {
                int currentSold = product.getSoldCount() != null ? product.getSoldCount() : 0;
                product.setSoldCount(currentSold + quantity);
                productRepository.save(product);
            });
        }
    }

    private void restoreInventory(Order order) {
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            return;
        }
        for (Order.OrderItem item : order.getItems()) {
            if (item == null || item.getProductId() == null || item.getQuantity() == null || item.getQuantity() <= 0) continue;
            productRepository.findById(item.getProductId()).ifPresent(product -> {
                if (product.getStock() != null) {
                    product.setStock(product.getStock() + item.getQuantity());
                    product.setInStock(product.getStock() > 0);
                    productRepository.save(product);
                }
            });
        }
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
