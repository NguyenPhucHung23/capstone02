package cap2.seeder;

import cap2.dto.request.ProductRequest;
import cap2.repository.ProductRepository;
import cap2.service.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductDataSeeder implements ApplicationRunner {

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @Override
    @SuppressWarnings("NullableProblems")
    public void run(ApplicationArguments args) {
        // if (productRepository.count() > 0) {
        //     log.info("Product data already exists in database. Skipping seeding.");
        //     return;
        // }

        log.info("Checking product data...");
        // Check if products already exist to avoid duplicate seeding (optional logic inside service handles updates too)
        // But if we want to run only once or if DB is empty:
        // Actually, the requirement is "import it into db". The provided JSON has sourceUrl/Provider which are unique keys.
        // The service method batchCreateOrUpdateProducts handles "create or update".
        // So I can just run it. However, to save startup time, I might want to check count.
        // Let's just run it as it's an "import" task requested by user.

        try {
            ClassPathResource resource = new ClassPathResource("data/products.json");
            if (!resource.exists()) {
                log.warn("File data/products.json not found. Skipping product seeding.");
                return;
            }

            log.info("Reading products from JSON file...");
            InputStream inputStream = resource.getInputStream();
            List<ProductRequest> requests = objectMapper.readValue(inputStream, new TypeReference<>() {});

            log.info("Found {} products in file. Starting import...", requests.size());

            // Mock authentication context for admin role
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(
                    "admin-seeder",
                    "N/A",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            ));
            SecurityContextHolder.setContext(context);

            try {
                productService.batchCreateOrUpdateProducts(requests);
                log.info("Product data seeded successfully.");
            } finally {
                SecurityContextHolder.clearContext();
            }

        } catch (IOException e) {
            log.error("Failed to seed product data", e);
        } catch (Exception e) {
            log.error("Error during product seeding", e);
        }
    }
}
