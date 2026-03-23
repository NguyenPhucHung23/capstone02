package cap2.repository;

import cap2.schema.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<Review, String> {

    Page<Review> findByProductId(String productId, Pageable pageable);

    Page<Review> findByUserId(String userId, Pageable pageable);

    boolean existsByUserIdAndProductId(String userId, String productId);

    void deleteByProductId(String productId);
}
