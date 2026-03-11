package cap2.repository;

import cap2.schema.DesignRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignRequestRepository extends MongoRepository<DesignRequest, String> {
}
