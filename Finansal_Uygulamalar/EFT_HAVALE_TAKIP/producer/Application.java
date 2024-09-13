package com.xbank.bigdata.efthavale.producer;

import com.xbank.bigdata.efthavale.producer.interfaces.DataGenerator;
import com.xbank.bigdata.efthavale.producer.interfaces.KafkaProducerService;
import io.github.cdimascio.dotenv.Dotenv;


public class Application {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String kafkaURL = dotenv.get("KAFKA_URL");

        if (kafkaURL == null || kafkaURL.isEmpty()) {
            throw new IllegalArgumentException("Kafka URL cannot be null or empty");
        }

        KafkaProducerService producerService = new ConcreteKafkaProducer(kafkaURL);
        DataGenerator dg = new ConcreteDataGenerator();

        while (true) {
            String data = dg.generate();
            producerService.Send("efthavale", data);
        }
    }
}
