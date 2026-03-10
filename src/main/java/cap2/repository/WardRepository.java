package cap2.repository;

import cap2.schema.Ward;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardRepository extends MongoRepository<Ward, String> {
    List<Ward> findByCityId(String cityId);
    boolean existsByNameAndCityId(String name, String cityId);
}
