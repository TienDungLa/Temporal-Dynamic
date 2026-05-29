package dunglt.temporal.base.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "m_kafka_config")
public class MKafkaConfig {

    @Id
    @Column(name = "kafka_config_id")
    private Integer kafkaConfigId;

    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "type")
    private String type; // send, response, notify

    @Column(name = "topic")
    private String topic;

    @Column(name = "bootstrap_servers")
    private String bootstrapServers; // Kafka broker addresses

    @Column(name = "group_id")
    private String groupId; // Kafka consumer group ID

    @Column(name = "time_out_ms")
    private Long timeOutMs;

}
