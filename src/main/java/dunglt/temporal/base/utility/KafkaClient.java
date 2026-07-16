package dunglt.temporal.base.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import dunglt.temporal.base.model.MKafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@Component
public class KafkaClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String FIELD_CORRELATION_ID = "requestId";
    private static final String FIELD_DATA = "data";

    public String sendMessage(Object data, MKafkaConfig config) {
        return sendMessage(data, config, UUID.randomUUID().toString());
    }

    public String sendMessage(Object data, MKafkaConfig config, String requestId) {
        if (config == null) {
            throw new RuntimeException("Kafka config is null");
        }

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            Map<String, Object> envelope = new LinkedHashMap<>();
            envelope.put(FIELD_CORRELATION_ID, requestId);
            envelope.put(FIELD_DATA, data);

            String message = objectMapper.writeValueAsString(envelope);
            ProducerRecord<String, String> record = new ProducerRecord<>(config.getTopic(), requestId, message);
            producer.send(record).get();
        } catch (Exception e) {
            throw new RuntimeException("Error sending message to Kafka: " + e.getMessage());
        }

        return "Message sent to Kafka topic: " + config.getTopic();
    }

    public Object waitForMessage(MKafkaConfig config, String correlationId) {
        if (config == null) {
            return null;
        }

        String groupId = (config.getGroupId() != null ? config.getGroupId() : "default-group");

        long timeoutMillis = config.getTimeOutMs() != null ? config.getTimeOutMs() : 30000;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(config.getTopic()));

            long startTime = System.currentTimeMillis();
            //polling loop until timeout or message received
            while (System.currentTimeMillis() - startTime < timeoutMillis) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    if (correlationId == null || correlationId.equals(record.key())) {
                        Map<?, ?> message = objectMapper.readValue(record.value(), Map.class);
                        return message;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error receiving message from Kafka: " + e.getMessage());
        }

        throw new RuntimeException("Timeout waiting for Kafka message on topic: " + config.getTopic()
                + " [correlationId=" + correlationId + "]");
    }
}

