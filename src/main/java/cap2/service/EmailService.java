package cap2.service;
 
import cap2.schema.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
 
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
 
@Service
@Slf4j
public class EmailService {
 
    @Value("${brevo.api.key}")
    private String apiKey;
 
    @Value("${brevo.from.email}")
    private String fromEmail;
 
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    private static final String SENDER_NAME = "Virtual Shop";
 
    // DTO records for Brevo direct REST API call
    private record Sender(String name, String email) {}
    private record Recipient(String email) {}
    private record BrevoEmailRequest(Sender sender, List<Recipient> to, String subject, String htmlContent) {}
 
    private void sendEmail(String toEmail, String subject, String htmlContent) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("api-key", apiKey);
 
            Sender sender = new Sender(SENDER_NAME, fromEmail);
            Recipient recipient = new Recipient(toEmail);
            BrevoEmailRequest requestBody = new BrevoEmailRequest(
                    sender,
                    Collections.singletonList(recipient),
                    subject,
                    htmlContent
            );
 
            HttpEntity<BrevoEmailRequest> request = new HttpEntity<>(requestBody, headers);
 
            log.info("Gửi email tới: {} qua Brevo REST API...", toEmail);
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, request, String.class);
 
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Đã gửi email tới: {}. Response: {}", toEmail, response.getBody());
            } else {
                log.error("Lỗi khi gửi email tới: {}. Status: {}, Response: {}", toEmail, response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Lỗi khi gửi email tới {} qua Brevo REST API: {}", toEmail, e.getMessage());
        }
    }
 
    @Async
    public void sendOtp(String to, String otp) {
        String subject = "Mã OTP của bạn";
        String htmlContent = "<h1>Mã OTP xác thực của bạn là: " + otp + "</h1><p>Mã này sẽ hết hạn sau 1 giờ.</p>";
        sendEmail(to, subject, htmlContent);
    }
 
    @Async
    public void sendOrderConfirmation(Order order) {
        String subject = "Xác nhận đơn hàng #" + order.getOrderCode();
        String htmlContent = buildOrderConfirmationContent(order);
        sendEmail(order.getCustomerEmail(), subject, htmlContent);
    }
 
    @Async
    public void sendOrderStatusUpdate(Order order) {
        String subject = "Cập nhật trạng thái đơn hàng #" + order.getOrderCode();
        String htmlContent = buildOrderStatusUpdateContent(order);
        sendEmail(order.getCustomerEmail(), subject, htmlContent);
    }
 
    @Async
    public void sendPaymentSuccessNotification(Order order) {
        String subject = "Thanh toán thành công cho đơn hàng #" + order.getOrderCode();
        String htmlContent = buildPaymentSuccessContent(order);
        sendEmail(order.getCustomerEmail(), subject, htmlContent);
    }
 
    private String buildOrderConfirmationContent(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>");
        sb.append("<h2 style='color: #2c3e50;'>Cảm ơn bạn đã đặt hàng tại Cap2!</h2>");
        sb.append("<p>Xin chào <strong>").append(order.getCustomerName()).append("</strong>,</p>");
        sb.append("<p>Đơn hàng của bạn đã được tiếp nhận và đang được xử lý.</p>");
        sb.append("<div style='background: #f9f9f9; padding: 15px; border-radius: 5px; margin-bottom: 20px;'>");
        sb.append("<h3 style='margin-top: 0;'>Thông tin đơn hàng:</h3>");
        sb.append("<p><strong>Mã đơn hàng:</strong> ").append(order.getOrderCode()).append("</p>");
        sb.append("<p><strong>Trạng thái:</strong> ").append(getStatusDisplay(order.getStatus())).append("</p>");
        sb.append("<p><strong>Phương thức thanh toán:</strong> ").append(order.getPaymentMethod()).append("</p>");
        sb.append("<p><strong>Tổng cộng:</strong> <span style='color: #e74c3c; font-weight: bold;'>").append(formatCurrency(order.getTotalAmount())).append("</span></p>");
        sb.append("</div>");
        sb.append("<h4>Sản phẩm:</h4>");
        sb.append("<table style='width: 100%; border-collapse: collapse;'>");
        sb.append("<tr style='background: #eee;'>");
        sb.append("<th style='padding: 10px; border: 1px solid #ddd;'>Sản phẩm</th>");
        sb.append("<th style='padding: 10px; border: 1px solid #ddd;'>Số lượng</th>");
        sb.append("<th style='padding: 10px; border: 1px solid #ddd;'>Thành tiền</th>");
        sb.append("</tr>");
        for (Order.OrderItem item : order.getItems()) {
            sb.append("<tr>");
            sb.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(item.getProductName()).append("</td>");
            sb.append("<td style='padding: 10px; border: 1px solid #ddd; text-align: center;'>").append(item.getQuantity()).append("</td>");
            sb.append("<td style='padding: 10px; border: 1px solid #ddd; text-align: right;'>").append(formatCurrency(item.getSubtotal())).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        sb.append("<div style='margin-top: 20px;'>");
        sb.append("<p><strong>Địa chỉ giao hàng:</strong> ").append(order.getShippingAddress()).append(", ")
                .append(order.getShippingWard()).append(", ")
                .append(order.getShippingCity() != null ? order.getShippingCity() : order.getShippingProvince())
                .append("</p>");
        sb.append("</div>");
        sb.append("<p style='margin-top: 30px;'>Cảm ơn bạn đã tin tưởng chúng tôi!</p>");
        sb.append("<p><strong>Virtual Shop</strong></p>");
        sb.append("</div>");
        return sb.toString();
    }
 
    private String buildOrderStatusUpdateContent(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>");
        sb.append("<h2 style='color: #2c3e50;'>Cập nhật trạng thái đơn hàng</h2>");
        sb.append("<p>Xin chào <strong>").append(order.getCustomerName()).append("</strong>,</p>");
        sb.append("<p>Trạng thái đơn hàng #<strong>").append(order.getOrderCode()).append("</strong> của bạn đã được thay đổi thành: <span style='color: #3498db; font-weight: bold;'>")
                .append(getStatusDisplay(order.getStatus())).append("</span></p>");
        sb.append("<div style='background: #f9f9f9; padding: 15px; border-radius: 5px; margin-top: 20px;'>");
        sb.append("<p>Bạn có thể vào website để theo dõi chi tiết hành trình đơn hàng.</p>");
        sb.append("</div>");
        sb.append("<p style='margin-top: 30px;'>Trân trọng!</p>");
        sb.append("<p><strong>Cap2 Team</strong></p>");
        sb.append("</div>");
        return sb.toString();
    }
 
    private String buildPaymentSuccessContent(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>");
        sb.append("<h2 style='color: #27ae60;'>Thanh toán thành công!</h2>");
        sb.append("<p>Xin chào <strong>").append(order.getCustomerName()).append("</strong>,</p>");
        sb.append("<p>Chúc mừng! Giao dịch thanh toán cho đơn hàng #<strong>").append(order.getOrderCode()).append("</strong> của bạn đã thành công.</p>");
        sb.append("<div style='background: #f9f9f9; padding: 15px; border-radius: 5px; margin-top: 20px;'>");
        sb.append("<p><strong>Trạng thái đơn hàng:</strong> <span style='color: #3498db; font-weight: bold;'>Đã xác nhận thanh toán</span></p>");
        sb.append("<p>Chúng tôi đang chuẩn bị hàng và sẽ sớm bàn giao cho đơn vị vận chuyển.</p>");
        sb.append("</div>");
        sb.append("<p style='margin-top: 30px;'>Cảm ơn bạn đã mua sắm tại Cap2!</p>");
        sb.append("<p><strong>Cap2 Team</strong></p>");
        sb.append("</div>");
        return sb.toString();
    }
 
    private String getStatusDisplay(Order.OrderStatus status) {
        return switch (status) {
            case PENDING -> "Đang chờ xử lý";
            case CONFIRMED -> "Đã xác nhận";
            case SHIPPING -> "Đang giao hàng";
            case DELIVERED -> "Giao hàng thành công";
            case CANCELLED -> "Đã hủy";
        };
    }
 
    private String formatCurrency(Double amount) {
        if (amount == null) return "0 ₫";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount).replace("₫", "₫");
    }
}
