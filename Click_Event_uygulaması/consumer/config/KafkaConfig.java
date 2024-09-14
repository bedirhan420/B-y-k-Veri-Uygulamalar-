package com.xcommerce.bigdata.clickevents.consumer.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.function.Consumer;

@Configuration
public class KafkaConfig {
    Dotenv dotenv = Dotenv.load();
    private final String kafkaURL = dotenv.get("KAFKA_URL");

    @Bean
    public KafkaConsumer<String, String> kafkaConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaURL);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "xcommerce01");
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "xcommercecl01");
        return new KafkaConsumer<>(config);
    }
}
