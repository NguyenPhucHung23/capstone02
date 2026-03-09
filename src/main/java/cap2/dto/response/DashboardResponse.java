package cap2.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardResponse {

    // ===== Overview Stats =====
    OverviewStats overview;

    // ===== Monthly Revenue =====
    List<MonthlyRevenue> monthlyRevenue;

    // ===== Recent Orders =====
    List<RecentOrder> recentOrders;

    // ===== Best Selling Products =====
    List<BestSellingProduct> bestSellingProducts;

    // ===== Order Status Summary =====
    OrderStatusSummary orderStatusSummary;

    // ============================================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OverviewStats {
        Double totalRevenue;              // Tổng doanh thu (đơn đã thanh toán)
        String totalRevenueFormatted;     // "458,500,000 ₫"
        Double revenueGrowthPercent;      // % tăng trưởng so với tháng trước

        Long totalNewOrders;              // Đơn hàng mới (PENDING)
        Double orderGrowthPercent;        // % tăng trưởng

        Long totalProducts;               // Tổng sản phẩm
        Double productGrowthPercent;      // % tăng trưởng

        Long totalCustomers;              // Tổng khách hàng
        Double customerGrowthPercent;     // % tăng trưởng

        Long totalOrders;                 // Tổng tất cả đơn hàng
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MonthlyRevenue {
        int year;
        int month;
        String monthLabel;        // "T1", "T2", ... "T12"
        Double revenue;
        Long orderCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RecentOrder {
        String id;
        String orderCode;
        String customerName;
        Double totalAmount;
        String status;
        String statusDisplay;
        String paymentMethod;
        String paymentMethodDisplay;
        String paymentStatus;
        Instant createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BestSellingProduct {
        String id;
        String name;
        String category;
        Double price;
        String priceFormatted;
        String image;            // Ảnh đầu tiên
        Integer soldCount;       // Đã bán
        Integer stock;           // Tồn kho
        Boolean inStock;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OrderStatusSummary {
        Long pending;
        Long confirmed;
        Long shipping;
        Long delivered;
        Long cancelled;
    }
}
