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
import java.util.Properties;
import java.util.UUID;

@Component
public class KafkaClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String sendMessage(Object data, MKafkaConfig config) {
        if (config == null) {
            throw new RuntimeException("Kafka config is null");
        }

        try {
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            KafkaProducer<String, String> producer = new KafkaProducer<>(props);

            String message = objectMapper.writeValueAsString(data);
            ProducerRecord<String, String> record = new ProducerRecord<>(config.getTopic(), message);

            producer.send(record).get();
            producer.close();

        } catch (Exception e) {
            throw new RuntimeException("Error sending message to Kafka: " + e.getMessage(), e);
        }

        return "Message sent to Kafka topic: " + config.getTopic();
    }

    public Object waitForMessage(MKafkaConfig config) {
        if (config == null) {
            throw new RuntimeException("Kafka config is null");
        }

        KafkaConsumer<String, String> consumer = null;
        String uniqueGroupId = config.getGroupId() + "-" + UUID.randomUUID().toString();

        try {
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, config.getGroupId() != null ? uniqueGroupId : "default-group");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

            consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Collections.singletonList(config.getTopic()));

            long startTime = System.currentTimeMillis();
            Object message = null;
            long timeoutMillis = config.getTimeOutMs() != null ? config.getTimeOutMs() : 30000; // Default 30s

            while (System.currentTimeMillis() - startTime < timeoutMillis) {
                System.out.println("Polling Kafka for messages...");
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> record : records) {
                    message = objectMapper.readValue(record.value(), Object.class);
                    consumer.close();
                    return message;
                }
            }

            consumer.close();

            if (message == null) {
                consumer.close();
                throw new RuntimeException("Timeout waiting for Kafka message");
            }

            return message;

        } catch (Exception e) {
            throw new RuntimeException("Error receiving message from Kafka: " + e.getMessage(), e);
        }finally {
            consumer.close();
        }
    }
}

