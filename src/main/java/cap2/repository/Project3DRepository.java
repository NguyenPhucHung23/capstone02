package cap2.repository;

import cap2.schema.Project3D;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Project3DRepository extends MongoRepository<Project3D, String> {
    List<Project3D> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Project3D> findByDesignRequestId(String designRequestId);
}
