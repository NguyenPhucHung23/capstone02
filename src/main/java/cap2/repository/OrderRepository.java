package cap2.repository;

import cap2.schema.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    Page<Order> findByUserId(String userId, Pageable pageable);
    Optional<Order> findByOrderCode(String orderCode);
    boolean existsByOrderCode(String orderCode);
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    // Dashboard queries
    long countByStatus(Order.OrderStatus status);
    List<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("{ 'paymentStatus': 'PAID', 'createdAt': { $gte: ?0, $lte: ?1 } }")
    List<Order> findPaidOrdersBetween(Instant start, Instant end);

    @Query("{ 'paymentStatus': 'PAID' }")
    List<Order> findAllPaidOrders();

    List<Order> findByCreatedAtBetween(Instant start, Instant end);
    long countByCreatedAtBetween(Instant start, Instant end);
}
