package com.xbank.bigdata.efthavale.producer;

import com.xbank.bigdata.efthavale.producer.interfaces.KafkaProducerService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;


public class ConcreteKafkaProducer implements KafkaProducerService {
    public final Producer<String, String> producer;

    public ConcreteKafkaProducer(String kafkaURL) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaURL);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(properties);
    }

    @Override
    public void Send(String topic, String data) {
        ProducerRecord<String,String> record = new ProducerRecord<String,String>("efthavale",data);
        producer.send(record);
    }

    @Override
    public void Close() {
        producer.close();
    }
}
