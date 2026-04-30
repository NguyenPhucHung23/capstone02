package cap2.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        
        // Log khi bắt đầu gọi API
        log.info(">>> API Called: [{}] {}", request.getMethod(), request.getRequestURI());
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            // Log khi kết thúc API kèm thời gian xử lý và HTTP status
            log.info("<<< API Finished: [{}] {} - Status: {} - Duration: {}ms", 
                     request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
        }
    }
}
