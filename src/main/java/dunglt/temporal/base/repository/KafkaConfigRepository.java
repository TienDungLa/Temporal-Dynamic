package dunglt.temporal.base.repository;

import dunglt.temporal.base.model.MKafkaConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KafkaConfigRepository extends JpaRepository<MKafkaConfig, Integer> {

    MKafkaConfig findByActivityIdAndType(Integer activityId, String type);
}
