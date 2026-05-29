package dunglt.temporal.base.service;

import dunglt.temporal.base.model.MKafkaConfig;
import dunglt.temporal.base.model.MRestConfig;
import dunglt.temporal.base.repository.KafkaConfigRepository;
import dunglt.temporal.base.repository.RestConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class ConnectionService {
    private final  RestConfigRepository restConfigRepository;
    private final KafkaConfigRepository kafkaConfigRepository;

    public ConnectionService(RestConfigRepository restConfigRepository, KafkaConfigRepository kafkaConfigRepository) {
        this.restConfigRepository = restConfigRepository;
        this.kafkaConfigRepository = kafkaConfigRepository;
    }

    public String createNewRestConfig(){

        return null;
    }


    public MRestConfig getRestConfigByActivityIdAndType(Integer activityId, String type) {
        return restConfigRepository.findByActivityIdAndType(activityId, type);
    }

    public MKafkaConfig getKafkaConfigByActivityIdAndType(Integer activityId, String type) {
        return kafkaConfigRepository.findByActivityIdAndType(activityId, type);
    }
}
