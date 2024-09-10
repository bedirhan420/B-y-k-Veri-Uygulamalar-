package com.bigdatacompany.eticaret;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class MessageProducer {
    private Producer<String, String> producer; // Use generic types for the Producer
    String kafkaProducer = System.getenv("KAFKA_SERVERS");
    @PostConstruct
    public void init() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Initialize the class field
        this.producer = new KafkaProducer<>(config);
    }

    public void send(String term) {
        ProducerRecord<String, String> rec = new ProducerRecord<>("searchv-analys-streaming", term);
        producer.send(rec);
    }

    public void close() {
        if (producer != null) {
            producer.close();
        }
    }
}
