package cap2.controller;

import cap2.dto.request.CreateOrderRequest;
import cap2.dto.response.ApiResponse;
import cap2.dto.response.OrderResponse;
import cap2.dto.response.PageResponse;
import cap2.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ApiResponse.ok("Tạo đơn hàng thành công", response);
    }

    @GetMapping
    public ApiResponse<PageResponse<OrderResponse>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderResponse> response = orderService.getMyOrders(page, size);
        return ApiResponse.ok("Lấy danh sách đơn hàng thành công", response);
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable String id) {
        OrderResponse response = orderService.getOrderById(id);
        return ApiResponse.ok("Lấy chi tiết đơn hàng thành công", response);
    }

    @GetMapping("/code/{orderCode}")
    public ApiResponse<OrderResponse> getOrderByCode(@PathVariable String orderCode) {
        OrderResponse response = orderService.getOrderByCode(orderCode);
        return ApiResponse.ok("Lấy chi tiết đơn hàng thành công", response);
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable String id) {
        OrderResponse response = orderService.cancelOrder(id);
        return ApiResponse.ok("Hủy đơn hàng thành công", response);
    }
}
