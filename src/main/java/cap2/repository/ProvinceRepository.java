package cap2.repository;

import cap2.schema.Province;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceRepository extends MongoRepository<Province, String> {
    Optional<Province> findByName(String name);
    List<Province> findByType(Province.Type type);
    boolean existsByName(String name);
}
