package com.xcommerce.bigdata.clickevents.consumer;

import com.xcommerce.bigdata.clickevents.consumer.client.ElasticSearchClient;
import com.xcommerce.bigdata.clickevents.consumer.config.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import java.time.Duration;
import java.util.Arrays;

public class Application {
    public static void main(String[] args) {
        ElasticSearchClient elasticSearchClient = new ElasticSearchClient();
        KafkaConfig kafkaConfig = new KafkaConfig();
        KafkaConsumer<String, String> kafkaConsumer = kafkaConfig.kafkaConsumer();
        kafkaConsumer.subscribe(Arrays.asList("clickevent"));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down consumer...");
            kafkaConsumer.wakeup();
        }));

        try {
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> rec : records) {
                    elasticSearchClient.indexDocument(rec);
                }
            }
        } catch (WakeupException e) {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            kafkaConsumer.close();
            elasticSearchClient.close();
        }
    }
}
