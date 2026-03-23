package cap2.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class AdminOrderFilterRequest {

    /** Tìm kiếm theo mã đơn, tên khách hàng, email */
    String keyword;

    /** Lọc theo trạng thái: PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED */
    String status;

    /** Lọc theo phương thức thanh toán: COD, VNPAY, MOMO */
    String paymentMethod;

    /** Lọc theo trạng thái thanh toán: PENDING, PAID, FAILED */
    String paymentStatus;

    /** Từ ngày (yyyy-MM-dd) */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate fromDate;

    /** Đến ngày (yyyy-MM-dd) */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate toDate;
}
