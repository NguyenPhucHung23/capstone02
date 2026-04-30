package cap2.config;

import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        log.info("✅ RequestLoggingFilter has been registered");
        System.out.println("✅ RequestLoggingFilter has been registered");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        log.info(">>> API Called: [{}] {}", request.getMethod(), request.getRequestURI());
        System.out.println(">>> API Called: [" + request.getMethod() + "] " + request.getRequestURI());

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            log.info("<<< API Finished: [{}] {} - Status: {} - Duration: {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);

            System.out.println("<<< API Finished: [" + request.getMethod() + "] "
                    + request.getRequestURI()
                    + " - Status: " + response.getStatus()
                    + " - Duration: " + duration + "ms");
        }
    }
}