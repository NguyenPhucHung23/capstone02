package cap2.controller;

import cap2.dto.request.UpdateOrderStatusRequest;
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
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminOrderController {

    OrderService orderService;

    @GetMapping
    public ApiResponse<PageResponse<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderResponse> response = orderService.getAllOrders(page, size);
        return ApiResponse.ok("Lấy danh sách đơn hàng thành công", response);
    }

    @GetMapping("/status/{status}")
    public ApiResponse<PageResponse<OrderResponse>> getOrdersByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderResponse> response = orderService.getOrdersByStatus(status, page, size);
        return ApiResponse.ok("Lấy danh sách đơn hàng thành công", response);
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable String id) {
        OrderResponse response = orderService.getOrderById(id);
        return ApiResponse.ok("Lấy chi tiết đơn hàng thành công", response);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, request);
        // Trả message khác nếu là hủy đơn
        String message = "CANCELLED".equalsIgnoreCase(request.getStatus())
                ? "Hủy đơn hàng thành công"
                : "Cập nhật trạng thái đơn hàng thành công";
        return ApiResponse.ok(message, response);
    }

    /**
     * Tìm kiếm & lọc đơn hàng (Admin)
     * GET /admin/orders/search
     */
    @GetMapping("/search")
    public ApiResponse<PageResponse<OrderResponse>> searchOrders(
            @ModelAttribute cap2.dto.request.AdminOrderFilterRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Tìm kiếm đơn hàng thành công",
                orderService.searchOrders(request, page, size));
    }
}
