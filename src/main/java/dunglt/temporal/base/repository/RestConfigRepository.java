package dunglt.temporal.base.repository;

import dunglt.temporal.base.model.MRestConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestConfigRepository extends JpaRepository<MRestConfig, Integer> {
    MRestConfig findByActivityIdAndType(Integer activityId, String type);
}
