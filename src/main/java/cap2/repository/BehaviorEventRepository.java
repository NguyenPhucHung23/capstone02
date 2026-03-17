package cap2.repository;

import cap2.schema.UserBehaviorEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BehaviorEventRepository extends MongoRepository<UserBehaviorEvent, String> {
}
