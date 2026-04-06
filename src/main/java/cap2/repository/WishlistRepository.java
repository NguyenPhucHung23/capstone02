package cap2.repository;

import cap2.schema.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WishlistRepository extends MongoRepository<Wishlist, String> {

    Optional<Wishlist> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
