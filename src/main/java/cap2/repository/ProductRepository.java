package cap2.repository;

import cap2.schema.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findBySourceProviderAndSourceUrl(String sourceProvider, String sourceUrl);
    boolean existsBySourceProviderAndSourceUrl(String sourceProvider, String sourceUrl);

    // Dashboard - best selling products sorted by soldCount desc
    List<Product> findAllByOrderBySoldCountDesc(Pageable pageable);
}
