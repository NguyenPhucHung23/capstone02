package cap2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // Local dev + FE Vercel
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",                     // Dev
                "https://virtualspace-lyart.vercel.app"     // Production
        ));

        // Cho phép các method thường dùng
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cho phép tất cả headers
        config.setAllowedHeaders(List.of("*"));

        // Cho phép gửi cookie/token nếu cần
        config.setAllowCredentials(true);

        // Áp dụng cho tất cả URL
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}