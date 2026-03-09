package cap2.controller;

import cap2.dto.response.ApiResponse;
import cap2.dto.response.DashboardResponse;
import cap2.service.AdminDashboardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminDashboardController {

    AdminDashboardService adminDashboardService;

    /**
     * Lấy toàn bộ dữ liệu dashboard trong 1 request
     * GET /admin/dashboard?recentOrders=5&topProducts=5
     */
    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard(
            @RequestParam(defaultValue = "5") int recentOrders,
            @RequestParam(defaultValue = "5") int topProducts) {
        DashboardResponse response = adminDashboardService.getDashboard(recentOrders, topProducts);
        return ApiResponse.ok("Lấy dữ liệu dashboard thành công", response);
    }

    /**
     * Lấy 4 thẻ thống kê tổng quan (Doanh thu, Đơn hàng, Sản phẩm, Khách hàng)
     * GET /admin/dashboard/overview
     */
    @GetMapping("/overview")
    public ApiResponse<DashboardResponse.OverviewStats> getOverviewStats() {
        DashboardResponse.OverviewStats stats = adminDashboardService.getOverviewStats();
        return ApiResponse.ok("Lấy thống kê tổng quan thành công", stats);
    }

    /**
     * Lấy dữ liệu doanh thu theo tháng (biểu đồ)
     * GET /admin/dashboard/revenue/monthly?months=12
     */
    @GetMapping("/revenue/monthly")
    public ApiResponse<List<DashboardResponse.MonthlyRevenue>> getMonthlyRevenue(
            @RequestParam(defaultValue = "12") int months) {
        List<DashboardResponse.MonthlyRevenue> data = adminDashboardService.getMonthlyRevenue(months);
        return ApiResponse.ok("Lấy doanh thu theo tháng thành công", data);
    }

    /**
     * Lấy đơn hàng gần đây
     * GET /admin/dashboard/orders/recent?limit=5
     */
    @GetMapping("/orders/recent")
    public ApiResponse<List<DashboardResponse.RecentOrder>> getRecentOrders(
            @RequestParam(defaultValue = "5") int limit) {
        List<DashboardResponse.RecentOrder> orders = adminDashboardService.getRecentOrders(limit);
        return ApiResponse.ok("Lấy đơn hàng gần đây thành công", orders);
    }

    /**
     * Lấy sản phẩm bán chạy nhất
     * GET /admin/dashboard/products/best-selling?limit=5
     */
    @GetMapping("/products/best-selling")
    public ApiResponse<List<DashboardResponse.BestSellingProduct>> getBestSellingProducts(
            @RequestParam(defaultValue = "5") int limit) {
        List<DashboardResponse.BestSellingProduct> products = adminDashboardService.getBestSellingProducts(limit);
        return ApiResponse.ok("Lấy sản phẩm bán chạy thành công", products);
    }

    /**
     * Lấy tổng kết trạng thái đơn hàng (số lượng mỗi trạng thái)
     * GET /admin/dashboard/orders/status-summary
     */
    @GetMapping("/orders/status-summary")
    public ApiResponse<DashboardResponse.OrderStatusSummary> getOrderStatusSummary() {
        DashboardResponse.OrderStatusSummary summary = adminDashboardService.getOrderStatusSummary();
        return ApiResponse.ok("Lấy tổng kết trạng thái đơn hàng thành công", summary);
    }
}
