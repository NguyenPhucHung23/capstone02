package cap2.repository;

import cap2.schema.DesignRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesignRequestRepository extends MongoRepository<DesignRequest, String> {
    List<DesignRequest> findByUserId(String userId);
    Page<DesignRequest> findByUserId(String userId, Pageable pageable);
}
